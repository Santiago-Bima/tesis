import { useState, useEffect } from 'react'

export function useModificacionesNuevas() {
  const [rol, setRol] = useState<string>('');
  const [nuevasModificaciones, setNuevasModificaciones] = useState<boolean>(false);

  useEffect(() => {
    const usuarioLogeado = window.sessionStorage.getItem("usuarioLogeado");
    
    if(usuarioLogeado != null) {
      setRol(JSON.parse(usuarioLogeado).rol)
    }
  }, [])
  

  const checkNewModification = async () => {
    if(rol == 'Subgerente'){
      try {
        const response = await fetch(`http://localhost:8082/lotes/modificaciones/validate`)
        if (response.ok) {
          const data = await response.json()

          if (data[0].nuevo) {
            setNuevasModificaciones(true)
          } else {
            setNuevasModificaciones(false)
          }

        } else {
          setNuevasModificaciones(false)
        }
      } catch (error) {
        console.error('Error checking for new dispatch:', error)
        setNuevasModificaciones(false)
      }
    }
  }

  useEffect(() => {
    checkNewModification()
    const interval = setInterval(checkNewModification, 5 * 60 * 1000) // Check every 5 minutes
    return () => clearInterval(interval)
  }, [rol])

  return { nuevasModificaciones, checkNewModification }
}