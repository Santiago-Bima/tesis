'use client'

import { useState, useEffect } from 'react'
import { useRouter } from 'next/navigation'
import Link from 'next/link'
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Alert, AlertDescription } from "@/components/ui/alert"
import { AlertCircle } from "lucide-react"
import { Checkbox } from "@/components/ui/checkbox"
import { useNavbar } from '@/contexts/NavbarContext'
import { useDespachoNuevo } from '@/hooks/useDespachoNuevo'
import { useControlStockNuevo } from '@/hooks/useControlStockNuevo'
import { useModificacionesNuevas } from '@/hooks/useModificacionesNuevas'
import { useFAQ } from '@/contexts/FAQContext'

export default function LoginPage() {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState<string | null>(null)
  const [termsAccepted, setTermsAccepted] = useState(false)
  const [showTerms, setShowTerms] = useState(true)
  const router = useRouter()
  const { reloadNavbar } = useNavbar();
  const { reloadFAQ } = useFAQ();
  const { checkNewControl } = useControlStockNuevo();
  const { checkNewDispatch } = useDespachoNuevo()
  const { checkNewModification } = useModificacionesNuevas()

  useEffect(() => {
    const acceptedTerms = sessionStorage.getItem('termsAccepted')
    if (acceptedTerms === 'true') {
      setTermsAccepted(true)
      setShowTerms(false)
    }
  }, [])

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError(null)

    if (!termsAccepted) {
      setError('Debe aceptar los términos y condiciones para continuar.')
      return
    }

    try {
      const result = await fetch("http://localhost:8082/autenticacion/login", {
        method: 'POST',
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          username: username,
          password: password
        })
      })

      if (!result.ok) {
        const errorData = await result.json()
        throw new Error(errorData.message || 'Error al iniciar sesión')
      }
      
      const data = await result.json()

      window.sessionStorage.setItem("usuarioLogeado", JSON.stringify(data))
      sessionStorage.setItem('termsAccepted', 'true')

      checkNewDispatch()
      checkNewControl()
      checkNewModification()
      reloadNavbar()
      reloadFAQ()
      router.push('/')
    } catch (error) {
      setError(error instanceof Error ? error.message : 'Ocurrió un error al iniciar sesión. Por favor, intente de nuevo más tarde.')
    }
  }

  return (
    <div className="flex items-center justify-center min-h-screen bg-gray-100 p-4">
      <Card className="w-full max-w-md">
        <CardHeader>
          <CardTitle className="text-xl sm:text-2xl font-bold text-center">Iniciar Sesión</CardTitle>
        </CardHeader>
        <CardContent>
          {error && (
            <Alert variant="destructive" className="mb-4 sm:mb-6">
              <AlertCircle className="h-4 w-4" />
              <AlertDescription>{error}</AlertDescription>
            </Alert>
          )}
          <form onSubmit={handleSubmit} className="space-y-3 sm:space-y-4">
            <div>
              <label htmlFor="username" className="block text-sm font-medium text-gray-700 mb-1">
                Nombre de usuario
              </label>
              <Input
                id="username"
                type="text"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                required
                className="w-full"
              />
            </div>
            <div>
              <label htmlFor="password" className="block text-sm font-medium text-gray-700 mb-1">
                Contraseña
              </label>
              <Input
                id="password"
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
                className="w-full"
              />
            </div>
            {showTerms && (
              <div className="flex items-center space-x-2">
                <Checkbox
                  id="terms"
                  checked={termsAccepted}
                  onCheckedChange={(checked) => setTermsAccepted(checked as boolean)}
                />
                <label htmlFor="terms" className="text-xs sm:text-sm text-gray-600">
                  Acepto los{' '}
                  <Link href="/terminosCondiciones" className="text-indigo-600 hover:underline">
                    Términos y Condiciones
                  </Link>
                </label>
              </div>
            )}
            <Button type="submit" className="w-full bg-indigo-600 hover:bg-indigo-700 text-white">
              Iniciar Sesión
            </Button>
          </form>
        </CardContent>
      </Card>
    </div>
  )
}