import Navbar from '@/components/Navbar'
import './globals.css'
import { Inter } from 'next/font/google'
import { NavbarProvider } from '@/contexts/NavbarContext'
import { FAQButton } from '@/components/FAQButton'
import { FAQProvider } from '@/contexts/FAQContext'

const inter = Inter({ subsets: ['latin'] })

export default async function RootLayout({
  children,
}: {
  children: React.ReactNode
}) {
  return (
    <html lang="en">
      <body className={inter.className}>
        <NavbarProvider>
          <FAQProvider>
            <Navbar />
            <main>{children}</main>
            <FAQButton />
          </FAQProvider>
        </NavbarProvider>
      </body>
    </html>
  )
}