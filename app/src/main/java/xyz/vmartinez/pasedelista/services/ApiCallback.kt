package xyz.vmartinez.pasedelista.services

interface ApiCallback<T> {
    fun onSuccess(respuesta: T)
    fun onError(error: String)
}