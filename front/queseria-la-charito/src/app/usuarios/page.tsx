'use client'

import { useState, useEffect } from 'react'
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Alert, AlertDescription } from "@/components/ui/alert"
import { AlertCircle } from 'lucide-react'
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog"
import { useRouter } from 'next/navigation'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"

interface User {
  username: string
  rol: string
}

export default function UsersPage() {
  const [users, setUsers] = useState<User[]>([])
  const [roles, setRoles] = useState<string[]>([])
  const [error, setError] = useState<string | null>(null)
  const [editingUser, setEditingUser] = useState<string | null>(null)
  const [newUsername, setNewUsername] = useState('')
  const [newUserRole, setNewUserRole] = useState('')
  const [newUserPassword, setNewUserPassword] = useState('')
  const [currentUser, setCurrentUser] = useState('')
  const router = useRouter();

  useEffect(() => {
    const usuarioLogeado = window.sessionStorage.getItem("usuarioLogeado");
    
    if(!usuarioLogeado) {
      router.push('/login')
    } else {
      const user = JSON.parse(usuarioLogeado);
      if(user.rol !== 'Gerente') {
        router.push('/')
      } else {
        setCurrentUser(user.username)
        fetchUsers(user.username)
        fetchRoles()
      }
    }
  }, [router])

  const fetchUsers = async (currentUser: string) => {
    try {
      const response = await fetch('http://localhost:8082/autenticacion/usuarios')
      if (!response.ok) {
        const errorData = await response.json()
        throw new Error(errorData.message || 'Error al cargar los usuarios')
      }
      const data = await response.json()
      setUsers(data.filter((user: User) => user.username !== currentUser))
    } catch (error) {
      setError(error instanceof Error ? error.message : 'Error al cargar los usuarios. Por favor, intente de nuevo más tarde.')
    }
  }

  const fetchRoles = async () => {
    try {
      const response = await fetch('http://localhost:8082/roles')
      if (!response.ok) {
        const errorData = await response.json()
        throw new Error(errorData.message || 'Error al cargar los roles')
      }
      const data = await response.json()
      setRoles(data)
    } catch (error) {
      setError(error instanceof Error ? error.message : 'Error al cargar los roles. Por favor, intente de nuevo más tarde.')
    }
  }

  const handleDelete = async (username: string) => {
    if (window.confirm(`¿Está seguro de que desea eliminar al usuario ${username}?`)) {
      try {
        const response = await fetch(`http://localhost:8082/autenticacion/usuarios/${username}`, {
          method: 'DELETE',
        })
        if (!response.ok) {
          const errorData = await response.json()
          throw new Error(errorData.message || 'Error al eliminar el usuario')
        }
        fetchUsers(currentUser)
      } catch (error) {
        setError(error instanceof Error ? error.message : 'Error al eliminar el usuario. Por favor, intente de nuevo más tarde.')
      }
    }
  }

  const handleEdit = async (username: string) => {
    try {
      const response = await fetch(`http://localhost:8082/autenticacion/usuarios/${username}?nombre=${newUsername}`, {
        method: 'PUT',
      })
      if (!response.ok) {
        const errorData = await response.json()
        throw new Error(errorData.message || 'Error al editar el usuario')
      }
      setEditingUser(null)
      setNewUsername('')
      fetchUsers(currentUser)
    } catch (error) {
      setError(error instanceof Error ? error.message :'Error al editar el usuario. Por favor, intente de nuevo más tarde.')
    }
  }

  const handleCreate = async () => {
    try {
      const response = await fetch('http://localhost:8082/autenticacion/registro', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          username: newUsername,
          password: newUserPassword,
          rol: newUserRole,
        }),
      })
      if (!response.ok) {
        const errorData = await response.json()
        throw new Error(errorData.message || 'Error al crear el usuario')
      }
      setNewUsername('')
      setNewUserPassword('')
      setNewUserRole('')
      fetchUsers(currentUser)
    } catch (error) {
      setError(error instanceof Error ? error.message : 'Error al crear el usuario. Por favor, intente de nuevo más tarde.')
    }
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <h1 className="text-3xl font-bold mb-6 text-indigo-700">Gestión de Usuarios</h1>

      {error && (
        <Alert variant="destructive" className="mb-6">
          <AlertCircle className="h-4 w-4" />
          <AlertDescription>{error}</AlertDescription>
        </Alert>
      )}

      <Card className="mb-6">
        <CardHeader>
          <CardTitle>Crear Nuevo Usuario</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="flex flex-col space-y-4 sm:flex-row sm:space-y-0 sm:space-x-4">
            <Input
              placeholder="Nombre de usuario"
              value={newUsername}
              onChange={(e) => setNewUsername(e.target.value)}
              className="flex-grow"
            />
            <Input
              type="password"
              placeholder="Contraseña"
              value={newUserPassword}
              onChange={(e) => setNewUserPassword(e.target.value)}
              className="flex-grow"
            />
            <Select onValueChange={setNewUserRole} value={newUserRole}>
              <SelectTrigger className="w-full sm:w-[180px]">
                <SelectValue placeholder="Seleccionar Rol" />
              </SelectTrigger>
              <SelectContent>
                {roles.map((role) => (
                  <SelectItem key={role} value={role}>{role}</SelectItem>
                ))}
              </SelectContent>
            </Select>
            <Button onClick={handleCreate} className="w-full sm:w-auto bg-green-500 hover:bg-green-600 text-white">Crear Usuario</Button>
          </div>
        </CardContent>
      </Card>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {users.map((user) => (
          <Card key={user.username}>
            <CardHeader>
              <CardTitle>{user.username}</CardTitle>
            </CardHeader>
            <CardContent>
              <p>Rol: {user.rol}</p>
              <div className="flex space-x-2 mt-4">
                <Dialog>
                  <DialogTrigger asChild>
                    <Button variant="outline" onClick={() => setEditingUser(user.username)} className="bg-yellow-500 hover:bg-yellow-600 text-black">Editar</Button>
                  </DialogTrigger>
                  <DialogContent>
                    <DialogHeader>
                      <DialogTitle>Editar Usuario</DialogTitle>
                    </DialogHeader>
                    <Input
                      placeholder={user.username}
                      value={newUsername}
                      onChange={(e) => setNewUsername(e.target.value)}
                    />
                    <Button onClick={() => handleEdit(user.username)} className="bg-green-500 hover:bg-green-600 text-white">Guardar Cambios</Button>
                  </DialogContent>
                </Dialog>
                <Button variant="destructive" onClick={() => handleDelete(user.username)} className="bg-red-500 hover:bg-red-600 text-white">Eliminar</Button>
              </div>
            </CardContent>
          </Card>
        ))}
      </div>
    </div>
  )
}