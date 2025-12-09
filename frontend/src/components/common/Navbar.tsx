import { Bell, Search } from "lucide-react";
import { useEffect, useState } from "react";

interface NavbarProps {
    isLoggedIn: boolean;
    onLoginClick: () => void; // Placeholder for navigation logic
    onRegisterClick: () => void; // Placeholder for navigation logic
}

export const Navbar: React.FC<NavbarProps> = ({ isLoggedIn, onLoginClick, onRegisterClick }) => {
  const [isScrolled, setIsScrolled] = useState(false);

  useEffect(() => {
    const handleScroll = () => setIsScrolled(window.scrollY > 0);
    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  return (
    <nav className={`fixed top-0 left-0 w-full z-50 transition-colors duration-300 ${isScrolled ? 'bg-[#141414]' : 'bg-gradient-to-b from-black/80 to-transparent'}`}>
      <div className="max-w-[1920px] mx-auto px-4 sm:px-12">
        <div className="flex items-center justify-between h-16">
          {/* Left Side: Logo & Links */}
          <div className="flex items-center gap-8">
            <span className="text-red-600 font-bold text-2xl tracking-wider cursor-pointer">MOVIETIME</span>
            <div className="hidden md:flex space-x-6">
              <a href="#" className="text-white text-sm font-medium hover:text-gray-300 transition">Home</a>
              <a href="#" className="text-gray-300 text-sm font-medium hover:text-white transition">TV Shows</a>
              <a href="#" className="text-gray-300 text-sm font-medium hover:text-white transition">Movies</a>
              {isLoggedIn && (
                  <a href="#" className="text-gray-300 text-sm font-medium hover:text-white transition">My List</a>
              )}
            </div>
          </div>

          {/* Right Side: Search, Auth, Profile */}
          <div className="flex items-center space-x-6">
             <Search className="text-white cursor-pointer hover:text-gray-300" size={20}/>
             
             {isLoggedIn ? (
                 // Logged In View
                 <>
                    <Bell className="text-white cursor-pointer hover:text-gray-300" size={20}/>
                    <div className="flex items-center gap-2 cursor-pointer group">
                        <div className="w-8 h-8 rounded bg-blue-600 flex items-center justify-center text-white font-bold text-sm">
                        S
                        </div>
                        <span className="text-xs text-white hidden group-hover:block">Salih</span>
                    </div>
                 </>
             ) : (
                 // Guest View (Login/Register)
                 <div className="flex items-center gap-4">
                    <button 
                        onClick={onLoginClick}
                        className="text-white hover:text-gray-300 text-sm font-medium transition-colors"
                    >
                        Login
                    </button>
                    <button 
                        onClick={onRegisterClick}
                        className="bg-red-600 hover:bg-red-700 text-white px-4 py-1.5 rounded text-sm font-medium transition-colors flex items-center gap-2"
                    >
                        Register
                    </button>
                 </div>
             )}
          </div>
        </div>
      </div>
    </nav>
  );
};