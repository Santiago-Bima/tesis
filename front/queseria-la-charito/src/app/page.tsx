'use client'

import { useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import Link from "next/link";
import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card";
import { Bell } from "lucide-react";
import { useDespachoNuevo } from "@/hooks/useDespachoNuevo";
import { useControlStockNuevo } from "@/hooks/useControlStockNuevo";
import { useModificacionesNuevas } from "@/hooks/useModificacionesNuevas";

interface User {
  username: string;
  rol: string;
}

interface CardLinkProps {
  href: string;
  title: string;
  description: string;
  showNotification?: boolean;
  className?: string;
}

const CardLink: React.FC<CardLinkProps> = ({ href, title, description, showNotification, className }) => (
  <Link href={href} className={`block ${className}`}>
    <Card className="h-full hover:shadow-lg transition-shadow">
      <CardHeader>
        <CardTitle className="flex items-center text-lg sm:text-xl">
          {title}
          {showNotification && (
            <Bell className="ml-2 h-5 w-5 text-yellow-500 animate-pulse" />
          )}
        </CardTitle>
      </CardHeader>
      <CardContent>
        <p className="text-sm sm:text-base">{description}</p>
      </CardContent>
    </Card>
  </Link>
);

export default function Home() {
  const router = useRouter();
  const [user, setUser] = useState<User | null>(null);
  const { despachoNuevo } = useDespachoNuevo()
  const { nuevos } = useControlStockNuevo()
  const { nuevasModificaciones } = useModificacionesNuevas();

  useEffect(() => {
    const usuarioLogeado = window.sessionStorage.getItem("usuarioLogeado");
    
    if(!usuarioLogeado) {
      router.push('/login');
    } else {
      setUser(JSON.parse(usuarioLogeado));
    }
  }, [router]);

  if (!user) {
    return null;
  }

  const renderOperarioGrid = () => (
    <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 sm:gap-6">
      <CardLink
        href="/elaboraciones"
        title="Elaboraciones"
        description="Gestionar elaboraciones de quesos"
      />
      <CardLink
        href="/lotes"
        title="Lotes"
        description="Ver y gestionar lotes de producción e insumos"
      />
      <CardLink
        href="/controlesStock/nuevo"
        title="Controles de Stock"
        description="Realizar controles de inventario"
      />
      <CardLink
        href="/despachos/miDespacho"
        title="Despacho"
        description="Gestionar despachos de productos"
        showNotification={despachoNuevo ? true: false}
      />
    </div>
  );

  const renderSubgerenteGrid = () => (
    <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 sm:gap-6">
      <CardLink
        href="/controlesStock"
        title="Controles de Stock"
        description="Supervisar controles de inventario"
        showNotification={nuevos}
      />
      <CardLink
        href="/despachos"
        title="Despachos"
        description="Gestionar y supervisar despachos"
      />
      <CardLink
        href="/modificaciones"
        title="Modificaciones de Lotes"
        description="Supervisar cambios realizados al stock"
        showNotification={nuevasModificaciones}
      />
      <CardLink
        href="/compras"
        title="Compra de Insumos"
        description="Gestionar compras de materias primas"
      />
    </div>
  );

  const renderGerenteGrid = () => (
    <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 sm:gap-6">
      <CardLink
        href="/despachos/informe"
        title="Informes de Despachos"
        description="Ver informes detallados de despachos"
      />
      <CardLink
        href="/compras/informe"
        title="Informes de Compras"
        description="Ver informes detallados de compras de insumos"
      />
      <CardLink
        href="/elaboraciones/informe"
        title="Informes de Elaboraciones"
        description="Ver informes detallados de elaboraciones de quesos"
        className="col-span-1 sm:col-span-2"
      />
    </div>
  );

  return (
    <div className="container mx-auto px-4 py-8">
      <header className="text-center mb-8 sm:mb-12">
        <h1 className="text-3xl sm:text-4xl font-bold text-indigo-700 mb-2 sm:mb-4">Bienvenido a Nuestra Quesería</h1>
        <p className="text-lg sm:text-xl text-gray-600 mb-2 sm:mb-4">Tradición y sabor en cada bocado</p>
        <p className="text-base sm:text-lg text-indigo-600">Hola, {user.username}</p>
      </header>
      {user.rol === 'Operario' && renderOperarioGrid()}
      {user.rol === 'Subgerente' && renderSubgerenteGrid()}
      {user.rol === 'Gerente' && renderGerenteGrid()}
    </div>
  );
}