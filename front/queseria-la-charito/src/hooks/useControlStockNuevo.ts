import { useState, useEffect } from 'react'

export function useControlStockNuevo() {
  const [rol, setRol] = useState<string>('');
  const [nuevos, setNuevos] = useState<boolean>(false);
  
  useEffect(() => {
    const usuarioLogeado = window.sessionStorage.getItem("usuarioLogeado");
    
    if(usuarioLogeado != null) {
      setRol(JSON.parse(usuarioLogeado).rol)
    }
  }, [])
  

  const checkNewControl = async () => {
    if(rol == 'Subgerente'){
      try {
        const response = await fetch(`http://localhost:8082/controles/validate`)
        if (response.ok) {
          const data = await response.json()
          
          if (data[0].nuevo) setNuevos(true)
          else setNuevos(false)

        } else {
          setNuevos(false)
        }
      } catch (error) {
        console.error('Error checking for new dispatch:', error)
        setNuevos(false)
      }
    }
  }

  useEffect(() => {
    checkNewControl()
    const interval = setInterval(checkNewControl, 5 * 60 * 1000) // Check every 5 minutes
    return () => clearInterval(interval)
  }, [rol])

  return { nuevos, checkNewControl }
}