/* eslint-disable react/no-unescaped-entities */
import Link from 'next/link'
import { Button } from "@/components/ui/button"

export default function TerminosYCondiciones() {
  return (
    <div className="container mx-auto px-4 py-8 bg-gray-50">
      <div className="max-w-3xl mx-auto bg-white p-8 rounded-lg shadow-md">
        <h1 className="text-3xl font-bold mb-6 text-indigo-700 border-b pb-2">Términos y Condiciones</h1>
        <div className="prose max-w-none">
          <p className="mb-4">
            Bienvenido a <span className="font-semibold">Sistema de Gestión de la Quesería "La Charito"</span> proporcionado por <span className="font-semibold">Nos</span>. Nos complace ofrecerle acceso al Servicio (como se define más abajo), sujeto a estos términos y condiciones (los "Términos de Servicio") y a la Política de Privacidad correspondiente de Santiago Bima. Al acceder y utilizar el Servicio, usted expresa su consentimiento, acuerdo y entendimiento de los Términos de Servicio y la Política de Privacidad. Si no está de acuerdo con los Términos de Servicio o la Política de Privacidad, no utilice el Servicio.
          </p>
          <p className="mb-4">
            Si utiliza el servicio está aceptando las modalidades operativas en vigencia descriptas más adelante, las declara conocer y aceptar, las que se habiliten en el futuro y en los términos y condiciones que a continuación se detallan:
          </p>

          <h2 className="text-2xl font-semibold mt-6 mb-3 text-indigo-600">Operaciones habilitadas</h2>
          <p className="mb-4">
            Las operaciones habilitadas son aquellas que estarán disponibles para los clientes, quienes deberán cumplir los requisitos que se encuentren vigentes en su momento para operar el Servicio. Las mismas podrán ser ampliadas o restringidas por el proveedor, comunicándolo previamente con una antelación no menor a 60 días, y comprenden entre otras, sin que pueda entenderse taxativamente las que se indican a continuación:
          </p>

          <h2 className="text-2xl font-semibold mt-6 mb-3 text-indigo-600">Transacciones</h2>
          <p className="mb-4">
            En ningún caso debe entenderse que la solicitud de un producto o servicio implica obligación alguna para el <span className="font-bold"><u>Acceso y uso del Servicio.</u></span>
          </p>
          <p className="mb-4">
            Para operar el Servicio se requerirá siempre que se trate de clientes de la quesería "La Charito", quienes podrán acceder mediante cualquier dispositivo con conexión a la Red Internet. El cliente deberá proporcionar el número de documento de identidad y la clave personal, que será provista por la aplicación como requisito previo a la primera operación, en la forma que le sea requerida. La clave personal y todo o cualquier otro mecanismo adicional de autenticación personal provisto por el Banco tiene el carácter de secreto e intransferible, y por lo tanto asumo las consecuencias de su divulgación a terceros, liberando a Santiago Bima de toda responsabilidad que de ello se derive. En ningún caso Santiago Bima requerirá que le suministre la totalidad de los datos, ni enviará mail requiriendo información personal alguna.
          </p>

          <h2 className="text-2xl font-semibold mt-6 mb-3 text-indigo-600">Costo del Servicio</h2>
          <p className="mb-4">
            La empresa de Santiago Bima podrá cobrar comisiones por el mantenimiento y/o uso de este Servicio o los que en el futuro implemente, entendiéndose facultado expresamente para efectuar los correspondientes débitos en mis cuentas, aún en descubierto, por lo que presto para ello mi expresa conformidad. En caso de cualquier modificación a la presente previsión, lo comunicará con al menos 60 días de antelación.
          </p>
          <p className="mb-4">
            El Usuario podrá dejar sin efecto la relación que surja de la presente, en forma inmediata, sin otra responsabilidad que la derivada de los gastos originados hasta ese momento. Si el cliente incumpliera cualquiera de las obligaciones asumidas en su relación contractual con empresa de Santiago Bima, o de los presentes Términos y Condiciones, el Banco podrá decretar la caducidad del presente Servicio en forma inmediata, sin que ello genere derecho a indemnización o compensación alguna. La empresa de Santiago Bima podrá dejar sin efecto la relación que surja de la presente, con un preaviso mínimo de 60 días, sin otra responsabilidad.
          </p>

          <h2 className="text-2xl font-semibold mt-6 mb-3 text-indigo-600">Validez de operaciones y notificaciones</h2>
          <p className="mb-4">
            Los registros emitidos por la app serán prueba suficiente de las operaciones cursadas por dicho canal. Renuncio expresamente a cuestionar la idoneidad o habilidad de ese medio de prueba. A los efectos del cumplimiento de disposiciones legales o contractuales, se otorga a las notificaciones por este medio el mismo alcance de las notificaciones mediante documento escrito.
          </p>

          <h2 className="text-2xl font-semibold mt-6 mb-3 text-indigo-600">Propiedad intelectual</h2>
          <p className="mb-4">
            El software en Argentina está protegido por la ley 11.723, que regula la propiedad intelectual y los derechos de autor de todos aquellos creadores de obras artísticas, literarias y científicas.
          </p>

          <h2 className="text-2xl font-semibold mt-6 mb-3 text-indigo-600">Privacidad de la información</h2>
          <p className="mb-4">
            Para utilizar los Servicios ofrecidos por Santiago Bima, los Usuarios deberán facilitar determinados datos de carácter personal. Su información personal se procesa y almacena en servidores o medios magnéticos que mantienen altos estándares de seguridad y protección tanto física como tecnológica. Para mayor información sobre la privacidad de los Datos Personales y casos en los que será revelada la información personal, se pueden consultar nuestras políticas de privacidad.
          </p>
        </div>
        <div className="mt-8">
          <Link href="/login">
            <Button className="bg-indigo-600 hover:bg-indigo-700 text-white font-bold py-2 px-4 rounded">
              Volver al Inicio de Sesión
            </Button>
          </Link>
        </div>
      </div>
    </div>
  )
}