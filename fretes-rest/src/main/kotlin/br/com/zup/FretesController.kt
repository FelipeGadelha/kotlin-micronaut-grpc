package br.com.zup

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import javax.inject.Inject

@Controller
class FretesController(@Inject val gRpcClient: FretesServiceGrpc.FretesServiceBlockingStub) {

    @Get("/api/fretes")
    fun calcula(@QueryValue cep: String): FreteRs {
        val request = CalculaFreteRq
            .newBuilder()
            .setCep(cep)
            .build()
        val response = gRpcClient.calculaFrete(request)
        return FreteRs(cep = response.cep, valor = response.valor)
    }
}

data class FreteRs(val cep: String, val valor: Double) { }