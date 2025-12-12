import { Bell, LogOut, Search } from "lucide-react";
import { useEffect, useState } from "react";

interface NavbarProps {
    isLoggedIn: boolean;
    userName?: string;
    onLoginClick: () => void; // Placeholder for navigation logic
    onRegisterClick: () => void; // Placeholder for navigation logic
    onLogoutClick: () => void;
}

export const Navbar: React.FC<NavbarProps> = ({ isLoggedIn, userName, onLoginClick, onRegisterClick, onLogoutClick }) => {
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
            <span className="text-red-600 font-bold text-2xl tracking-wider cursor-pointer">UPCOMINGS</span>
            <div className="hidden md:flex space-x-6">
              <a href="#" className="text-white text-sm font-medium hover:text-gray-300 transition">Home</a>
              {isLoggedIn && (
                  <a href="#" className="text-gray-300 text-sm font-medium hover:text-white transition">My List</a>
              )}
            </div>
          </div>

          {/* Right Side: Search, Auth, Profile */}
          <div className="flex items-center space-x-6">
             <Search className="text-white cursor-pointer hover:text-gray-300" size={20}/>
             
             {isLoggedIn ? (
                 <>
                    <Bell className="text-white cursor-pointer hover:text-gray-300" size={20}/>
                    <div className="flex items-center gap-2 cursor-pointer group relative">
                        <div className="w-8 h-8 rounded bg-blue-600 flex items-center justify-center text-white font-bold text-sm uppercase">
                            {userName ? userName[0] : 'U'}
                        </div>
                        <span className="text-xs text-white hidden md:block">{userName}</span> {/*group-hover:block */}
                        
                        {/* Dropdown for Logout */}
                        <div className="absolute right-0 top-full mt-2 w-32 bg-[#181818] border border-gray-700 rounded shadow-lg opacity-0 invisible group-hover:opacity-100 group-hover:visible transition-all duration-200">
                            <button 
                                onClick={onLogoutClick}
                                className="w-full text-left px-4 py-3 text-sm text-white hover:bg-gray-800 flex items-center gap-2"
                            >
                                <LogOut size={14} /> Logout
                            </button>
                        </div>
                    </div>
                 </>
             ) : (
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