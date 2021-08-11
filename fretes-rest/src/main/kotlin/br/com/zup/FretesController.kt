package br.com.zup

import com.google.protobuf.Any
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.protobuf.StatusProto
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.exceptions.HttpStatusException
import javax.inject.Inject

@Controller
class FretesController(@Inject val gRpcClient: FretesServiceGrpc.FretesServiceBlockingStub) {

    @Get("/api/fretes")
    fun calcula(@QueryValue cep: String): FreteRs {
        val request = CalculaFreteRq
            .newBuilder()
            .setCep(cep)
            .build()
        try {
        val response = gRpcClient.calculaFrete(request)
        return FreteRs(cep = response.cep, valor = response.valor)
        } catch (ex: StatusRuntimeException){
            val description = ex.status.description
            val statusCode = ex.status.code
            if (statusCode == Status.Code.INVALID_ARGUMENT){
                throw HttpStatusException(HttpStatus.BAD_REQUEST, description)
            }
            if (statusCode == Status.Code.PERMISSION_DENIED){
                val statusProto = StatusProto
                    .fromThrowable(ex) ?: throw HttpStatusException(HttpStatus.FORBIDDEN, description)
                val anyDetails: Any = statusProto.detailsList[0]
                val errorDetails = anyDetails.unpack(ErrorDetails::class.java)
                throw HttpStatusException(HttpStatus.FORBIDDEN, "${errorDetails.code}: ${errorDetails.message}")
            }
            throw HttpStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.message)
        }
    }
}

data class FreteRs(val cep: String, val valor: Double) { }