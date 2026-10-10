package xyz.vmartinez.pasedelista.services

import xyz.vmartinez.pasedelista.models.AsistenciaModel
import xyz.vmartinez.pasedelista.models.InicioDeSesionModel
import xyz.vmartinez.pasedelista.models.RespuestaModel
import xyz.vmartinez.pasedelista.models.TokenModel

class AsistenciaService {

    fun registraAsistencia(asistenciaModel: AsistenciaModel): RespuestaModel{
        return RespuestaModel(12, "Asistencia")
    }

    fun subirFoto(byte: Byte){

    }

    fun iniciarSesion(inicioDeSesionModel: InicioDeSesionModel): TokenModel{
        return TokenModel("Token")
    }
}