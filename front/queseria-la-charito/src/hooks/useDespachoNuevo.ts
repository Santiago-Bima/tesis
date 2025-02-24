import { useState, useEffect } from 'react'

interface DespachoNuevo {
  id: number
}



export function useDespachoNuevo() {
  const [username, setUsername] = useState<string>('');
  const [rol, setRol] = useState<string>('');
  const [despachoNuevo, setDespachoNuevo] = useState<DespachoNuevo | null>(null)

  useEffect(() => {
    const usuarioLogeado = window.sessionStorage.getItem("usuarioLogeado");
    
    if(usuarioLogeado != null) {
      setUsername(JSON.parse(usuarioLogeado).username)
      setRol(JSON.parse(usuarioLogeado).rol)
    }
  }, [])

  const checkNewDispatch = async () => {
    if(rol === 'Operario'){
      try {
        const response = await fetch(`http://localhost:8082/despachos/mi-despacho/${username}`)
        if (response.ok) {
          const data = await response.json()
          setDespachoNuevo(data.length === 0 ? null : data[0])
        } else {
          setDespachoNuevo(null)
        }
      } catch (error) {
        console.error('Error checking for new dispatch:', error)
        setDespachoNuevo(null)
      }
    }
  }

  useEffect(() => {
    checkNewDispatch()
    const interval = setInterval(checkNewDispatch, 5 * 60 * 1000) // Check every 5 minutes
    return () => clearInterval(interval)
  }, [username])

  return { despachoNuevo, checkNewDispatch }
}