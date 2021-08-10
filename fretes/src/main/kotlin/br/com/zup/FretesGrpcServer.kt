package br.com.zup

import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class FretesGrpcServer: FretesServiceGrpc.FretesServiceImplBase() {

    private val log = LoggerFactory.getLogger(FretesGrpcServer::class.java)

    override fun calculaFrete(request: CalculaFreteRq?, responseObserver: StreamObserver<CalculaFreteRs>?) {
        log.info("Calculando frete para request: $request")

        val response = CalculaFreteRs
            .newBuilder()
            .setCep(request!!.cep)
            .setValor(Random.nextDouble(from = 0.0, until = 140.0))
            .build()

        log.info("Frete calculado: $response")
        responseObserver!!.onNext(response)
        responseObserver.onCompleted()
    }
}