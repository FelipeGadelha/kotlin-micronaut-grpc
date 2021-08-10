package br.com.zup

import com.google.protobuf.Any
import com.google.rpc.Code

import io.grpc.Status
import io.grpc.protobuf.StatusProto
import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class FretesGrpcServer: FretesServiceGrpc.FretesServiceImplBase() {

    private val log = LoggerFactory.getLogger(FretesGrpcServer::class.java)

    override fun calculaFrete(request: CalculaFreteRq?, responseObserver: StreamObserver<CalculaFreteRs>?) {
        log.info("Calculando frete para request: $request")

        val cep = request?.cep
        if (cep == null || cep.isBlank()) {
            val ex = Status.INVALID_ARGUMENT
                .withDescription("cep deve ser informado")
                .asRuntimeException()
            responseObserver?.onError(ex)
        }
        if (!cep!!.matches("[0-9]{5}-[0-9]{3}".toRegex())) {
            val ex = Status.INVALID_ARGUMENT
                .withDescription("cep inválido")
                .augmentDescription("formato esperado deve ser 99999-999")
                .asRuntimeException()
            responseObserver?.onError(ex)
        }

        if (cep.endsWith("333")){
            val statusProto = com.google.rpc.Status.newBuilder()
                .setCode(Code.PERMISSION_DENIED.number)
                .setMessage("usuário não pode acessar esse recurso")
                .addDetails(Any.pack(ErrorDetails.newBuilder()
                    .setCode(401)
                    .setMessage("token expirado")
                    .build()))
                .build()
            val ex = StatusProto.toStatusRuntimeException(statusProto)
            responseObserver?.onError(ex)
        }

        var valor = 0.0
        try {
            valor = Random.nextDouble(from = 0.0, until = 140.0)
            if (valor > 100.0) {
                throw IllegalStateException("Erro inesperado ao executar logica de negócio!")
            }
        } catch (ex: Exception) {
            responseObserver?.onError(
                Status.INTERNAL
                    .withDescription(ex.message)
                    .withCause(ex)
                    .asRuntimeException()

            )
        }
        val response = CalculaFreteRs
            .newBuilder()
            .setCep(request!!.cep)
            .setValor(valor)
            .build()

        log.info("Frete calculado: $response")
        responseObserver!!.onNext(response)
        responseObserver.onCompleted()
    }
}