import { NextRequest, NextResponse } from 'next/server'

export async function POST(req: NextRequest) {
  const { destino } = await req.json()

  if (!destino) {
    return NextResponse.json({ message: 'Destino is required' }, { status: 400 })
  }

  const origin = '-31.7168541862998, -64.40381056376108'
  const destination = `${destino.calle} ${destino.numero}, ${destino.barrio}, Cordoba, Argentina`

  try {
    const geocodeUrl = `https://maps.googleapis.com/maps/api/geocode/json?address=${encodeURIComponent(destination)}&key=${process.env.GOOGLE_MAPS_API_KEY}`
    const geocodeResponse = await fetch(geocodeUrl)
    const geocodeData = await geocodeResponse.json()

    if (geocodeData.status !== 'OK') {
      throw new Error(`Geocoding error: ${geocodeData.status} - ${geocodeData.error_message || 'Unknown error'}`)
    }

    if (geocodeData.results.length === 0) {
      throw new Error('No se pudo encontrar la ubicación del destino')
    }

    const { lat, lng } = geocodeData.results[0].geometry.location

    const distanceUrl = `https://maps.googleapis.com/maps/api/distancematrix/json?origins=${origin}&destinations=${lat},${lng}&mode=driving&key=${process.env.GOOGLE_MAPS_API_KEY}`
    const distanceResponse = await fetch(distanceUrl)
    const distanceData = await distanceResponse.json()

    if (distanceData.status !== 'OK') {
      throw new Error(`Distance Matrix error: ${distanceData.status} - ${distanceData.error_message || 'Unknown error'}`)
    }

    if (distanceData.rows[0].elements[0].status === 'OK') {
      return NextResponse.json({
        distance: distanceData.rows[0].elements[0].distance.text,
        duration: distanceData.rows[0].elements[0].duration.text
      })
    } else {
      throw new Error(`No se pudo calcular la distancia y duración del viaje: ${distanceData.rows[0].elements[0].status}`)
    }
  } catch (error) {
    console.error('Error al calcular la distancia:', error)
    return NextResponse.json({ message: 'Error al calcular la distancia', error: error instanceof Error ? error.message : "" }, { status: 500 })
  }
}