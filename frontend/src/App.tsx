import React, { useState, useEffect, useRef } from 'react';
import { Heart, X, User, Play, Info, Bell, Search, LogIn } from 'lucide-react';
import type { MovieData } from './types/movie';


// --- Mock Data Generator ---
const generateMockMovies = (count: number, type: 'upcoming' | 'now_playing' | 'top_picks'): MovieData[] => {
  return Array.from({ length: count }).map((_, i) => ({
    id: i + (type === 'upcoming' ? 100 : type === 'now_playing' ? 200 : 300),
    title: type === 'top_picks' ? `Recommended Movie ${i + 1}` : type === 'upcoming' ? `Upcoming Hit ${i + 1}` : `Now Playing ${i + 1}`,
    original_title: 'Original Title',
    overview: `This movie matches your taste in genres and features stunning visuals. Dive into the story of Movie ${i+1} and experience the thrill.`,
    release_date: '2023-12-01',
    poster_path: `https://placehold.co/300x450/1a1a1a/ffffff?text=Movie+${i+1}`, 
    backdrop_path: `https://placehold.co/1280x720/1a1a1a/ffffff?text=Backdrop+${i+1}`,
    popularity: 8.5 + (Math.random() * 2),
    vote_average: 7.5 + (Math.random() * 2),
    vote_count: Math.floor(Math.random() * 1000),
    genre_ids: [28, 12],
    original_language: 'en',
    adult: false,
    video: false,
  }));
};

// --- Components ---

interface NavbarProps {
    isLoggedIn: boolean;
    onLoginClick: () => void; // Placeholder for navigation logic
    onRegisterClick: () => void; // Placeholder for navigation logic
}

const Navbar: React.FC<NavbarProps> = ({ isLoggedIn, onLoginClick, onRegisterClick }) => {
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

interface MovieCardProps {
  movie: MovieData;
  onMovieClick: (movie: MovieData) => void;
}

const MovieCard: React.FC<MovieCardProps> = ({ movie, onMovieClick }) => {
  const [isFavorite, setIsFavorite] = useState(false);

  const handleFavoriteClick = (e: React.MouseEvent) => {
    e.stopPropagation();
    setIsFavorite(!isFavorite);
    // API logic here (POST/DELETE)
  };

  return (
    <div 
      onClick={() => onMovieClick(movie)}
      className="relative group w-[160px] md:w-[220px] flex-shrink-0 cursor-pointer transition-all duration-300 hover:scale-105 hover:z-10 rounded-md overflow-hidden"
    >
      {/* Poster */}
      <div className="aspect-[2/3] bg-gray-800">
        <img 
          src={movie.poster_path} 
          alt={movie.title} 
          className="w-full h-full object-cover"
          draggable="false" // Prevent image drag ghosting
        />
      </div>

      {/* Hover Info */}
      <div className="absolute inset-0 bg-gradient-to-t from-black/90 via-black/40 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-300 flex flex-col justify-end p-3">
         <div className="flex items-center gap-2 mb-2">
            <button className="w-8 h-8 rounded-full bg-white flex items-center justify-center hover:bg-gray-200 transition">
              <Play size={14} className="fill-black text-black ml-0.5"/>
            </button>
            <button onClick={handleFavoriteClick} className="w-8 h-8 rounded-full border-2 border-gray-400 flex items-center justify-center hover:border-white transition">
               <Heart size={14} className={isFavorite ? "fill-red-500 text-red-500" : "text-white"} />
            </button>
         </div>
         <h3 className="text-white font-bold text-xs md:text-sm mb-1">{movie.title}</h3>
         <div className="flex items-center gap-2 text-[10px] text-gray-300 font-semibold">
            <span className="text-green-500">{Math.round(movie.vote_average * 10)}% Match</span>
            <span className="border border-gray-500 px-1">HD</span>
         </div>
      </div>
    </div>
  );
};

interface MovieSliderProps {
  title: string;
  movies: MovieData[];
  onMovieClick: (movie: MovieData) => void;
}

const MovieSlider: React.FC<MovieSliderProps> = ({ title, movies, onMovieClick }) => {
  const scrollRef = useRef<HTMLDivElement>(null);
  
  // Refs for smooth hover scrolling logic
  const speedRef = useRef(0);
  const animationFrameId = useRef<number | null>(null);

  // Start the animation loop if it isn't running
  const startScrolling = () => {
    if (animationFrameId.current !== null) return;

    const loop = () => {
      if (scrollRef.current && speedRef.current !== 0) {
        scrollRef.current.scrollLeft += speedRef.current;
        animationFrameId.current = requestAnimationFrame(loop);
      } else {
        stopScrolling();
      }
    };
    animationFrameId.current = requestAnimationFrame(loop);
  };

  // Stop the animation loop
  const stopScrolling = () => {
    if (animationFrameId.current !== null) {
      cancelAnimationFrame(animationFrameId.current);
      animationFrameId.current = null;
    }
  };

  const handleMouseMove = (e: React.MouseEvent) => {
    if (!scrollRef.current) return;

    const { left, width } = scrollRef.current.getBoundingClientRect();
    const x = e.clientX - left;
    
    // Define "Active Zones" at edges (e.g., 150px width)
    const edgeThreshold = 150; 
    const maxSpeed = 12; // Pixels per frame (Increase for faster scroll)

    // If mouse is near LEFT edge
    if (x < edgeThreshold) {
       // Calculate intensity: Closer to edge = faster
       const intensity = (edgeThreshold - x) / edgeThreshold;
       speedRef.current = -maxSpeed * intensity;
       startScrolling();
    } 
    // If mouse is near RIGHT edge
    else if (x > width - edgeThreshold) {
       const intensity = (x - (width - edgeThreshold)) / edgeThreshold;
       speedRef.current = maxSpeed * intensity;
       startScrolling();
    } 
    // If mouse is in CENTER (Safe zone)
    else {
       speedRef.current = 0;
       stopScrolling();
    }
  };

  const handleMouseLeave = () => {
    speedRef.current = 0;
    stopScrolling();
  };

  // Cleanup on unmount
  useEffect(() => {
    return () => stopScrolling();
  }, []);

  return (
    <div className="mb-10 relative group/slider px-4 md:px-12">
      <h2 className="text-lg md:text-xl font-semibold text-white mb-3 hover:text-gray-300 cursor-pointer transition-colors inline-block">
        {title} <span className="text-xs text-blue-400 opacity-0 group-hover/slider:opacity-100 transition-opacity ml-2 font-normal">Explore All &gt;</span>
      </h2>
      
      <div className="relative">
        
        {/* Visual Indicators for Hover Zones (Optional: Fades in when hovering) */}
        <div className="absolute left-0 top-0 bottom-0 w-24 bg-gradient-to-r from-[#141414] to-transparent z-10 pointer-events-none opacity-0 group-hover/slider:opacity-100 transition-opacity duration-500" />
        <div className="absolute right-0 top-0 bottom-0 w-24 bg-gradient-to-l from-[#141414] to-transparent z-10 pointer-events-none opacity-0 group-hover/slider:opacity-100 transition-opacity duration-500" />

        {/* Slider Track */}
        <div 
          ref={scrollRef}
          className="flex space-x-4 overflow-x-auto scrollbar-hide pb-4 pt-2"
          style={{ scrollbarWidth: 'none', msOverflowStyle: 'none' }}
          onMouseMove={handleMouseMove}
          onMouseLeave={handleMouseLeave}
        >
          {movies.map(movie => (
            <MovieCard 
              key={movie.id} 
              movie={movie} 
              onMovieClick={onMovieClick}
            />
          ))}
          
          {/* Spacer for end of list */}
          <div className="w-12 flex-shrink-0" /> 
        </div>

      </div>
    </div>
  );
};

// 4. Movie Details Modal (Unchanged)
interface ModalProps {
  movie: MovieData | null;
  isOpen: boolean;
  onClose: () => void;
}

const MovieDetailsModal: React.FC<ModalProps> = ({ movie, isOpen, onClose }) => {
  if (!isOpen || !movie) return null;

  return (
    <div className="fixed inset-0 z-[60] flex items-center justify-center px-4 overflow-y-auto pt-10 pb-10">
      <div className="absolute inset-0 bg-black/70 backdrop-blur-sm" onClick={onClose} />

      <div className="relative bg-[#181818] rounded-lg shadow-2xl w-full max-w-4xl overflow-hidden animate-in fade-in zoom-in-95 duration-200">
        <button onClick={onClose} className="absolute top-4 right-4 z-20 p-2 bg-[#181818] rounded-full text-white hover:bg-gray-700">
          <X size={24} />
        </button>

        <div className="relative h-96">
          <div className="absolute inset-0 bg-gradient-to-t from-[#181818] to-transparent z-10" />
          <img 
            src={movie.backdrop_path.includes('http') ? movie.backdrop_path : `https://image.tmdb.org/t/p/original${movie.backdrop_path}`} 
            alt={movie.title} 
            className="w-full h-full object-cover"
          />
          <div className="absolute bottom-10 left-8 z-20 max-w-lg">
             <h2 className="text-4xl md:text-5xl font-bold mb-4">{movie.title}</h2>
             <div className="flex gap-3">
                <button className="bg-white text-black px-8 py-2 rounded font-bold hover:bg-opacity-80 flex items-center gap-2">
                   <Play size={20} className="fill-black" /> Play
                </button>
                <button className="bg-gray-500/40 text-white px-8 py-2 rounded font-bold hover:bg-gray-500/60 flex items-center gap-2">
                   <Heart size={20} /> My List
                </button>
             </div>
          </div>
        </div>

        <div className="p-8 grid md:grid-cols-[2fr_1fr] gap-8">
          <div className="space-y-4">
            <div className="flex items-center gap-4 text-sm">
              <span className="text-green-400 font-bold">{Math.round(movie.vote_average * 10)}% Match</span>
              <span>{movie.release_date}</span>
              <span className="border px-1 text-xs">HD</span>
            </div>
            <p className="text-gray-300 text-sm leading-relaxed">{movie.overview}</p>
          </div>
          <div className="text-sm space-y-3 text-gray-400">
             <div><span className="text-gray-500">Genres:</span> Action, Thriller</div>
             <div><span className="text-gray-500">Original Language:</span> {movie.original_language.toUpperCase()}</div>
             <div><span className="text-gray-500">Total Votes:</span> {movie.vote_count}</div>
          </div>
        </div>
      </div>
    </div>
  );
};

// 5. Main App Component
export default function App() {
  const [upcomingMovies, setUpcomingMovies] = useState<MovieData[]>([]);
  const [nowPlayingMovies, setNowPlayingMovies] = useState<MovieData[]>([]);
  const [topPicks, setTopPicks] = useState<MovieData[]>([]);
  const [selectedMovie, setSelectedMovie] = useState<MovieData | null>(null);
  const [isModalOpen, setIsModalOpen] = useState(false);

  // Temp state to simulate login toggle
  const [isLoggedIn, setIsLoggedIn] = useState(false);

  useEffect(() => {
    setUpcomingMovies(generateMockMovies(12, 'upcoming'));
    setNowPlayingMovies(generateMockMovies(12, 'now_playing'));
    setTopPicks(generateMockMovies(12, 'top_picks'));
  }, []);

  const handleMovieClick = (movie: MovieData) => {
    setSelectedMovie(movie);
    setIsModalOpen(true);
  };

  return (
    <div className="min-h-screen bg-[#141414] text-white font-sans overflow-x-hidden">
      <Navbar 
        isLoggedIn={isLoggedIn}
        onLoginClick={() => setIsLoggedIn(true)} // Simulate Login
        onRegisterClick={() => alert("Navigating to Register Page...")}
      />

      {/* HERO SECTION */}
      <div className="relative h-[85vh] w-full mb-4">
          <div className="absolute inset-0">
            <img 
              src="https://placehold.co/1920x1080/1a1a1a/ffffff?text=Featured+Blockbuster" 
              className="w-full h-full object-cover"
              alt="Featured"
            />
            <div className="absolute inset-0 bg-gradient-to-r from-[#141414] via-[#141414]/30 to-transparent" />
            <div className="absolute inset-0 bg-gradient-to-t from-[#141414] via-transparent to-transparent" />
          </div>
          
          <div className="absolute top-[45%] left-0 transform -translate-y-1/2 p-8 md:p-16 space-y-6 max-w-2xl z-10">
             {/* Conditional Personal Greeting */}
             {isLoggedIn && (
                <div className="flex items-center gap-2 text-gray-300 font-medium tracking-wide uppercase text-sm animate-in fade-in slide-in-from-left-4 duration-500">
                    <span className="w-1 h-4 bg-red-600 block"></span>
                    Welcome back, Salih!
                </div>
             )}

             <h1 className="text-5xl md:text-7xl font-black drop-shadow-2xl leading-tight">
               INTERSTELLAR <br/> <span className="text-3xl md:text-4xl font-light text-gray-200">ODYSSEY</span>
             </h1>
             
             <p className="text-lg text-gray-200 drop-shadow-md line-clamp-3">
               Prepare for the ultimate journey beyond the stars. The fate of humanity rests in the hands of a few brave explorers in this cinematic masterpiece.
             </p>
             
             <div className="flex gap-4 pt-2">
                <button className="bg-white text-black px-8 py-3 rounded font-bold hover:bg-gray-200 transition flex items-center gap-2 text-lg">
                   <Play className="fill-black" size={24} /> Play
                </button>
                <button className="bg-gray-500/40 backdrop-blur-md text-white px-8 py-3 rounded font-bold hover:bg-gray-500/60 transition flex items-center gap-2 text-lg">
                   <Info size={24} /> More Info
                </button>
             </div>
          </div>
      </div>

      {/* Content Rows */}
      <div className="relative z-20 space-y-2 md:-mt-24 pl-0">
        {isLoggedIn && (
            <MovieSlider 
                title="Top Picks for Salih" 
                movies={topPicks} 
                onMovieClick={handleMovieClick}
            />
        )}
        
        <MovieSlider 
          title="Upcoming Movies" 
          movies={upcomingMovies} 
          onMovieClick={handleMovieClick}
        />
        
        <MovieSlider 
          title="Now Playing in Theaters" 
          movies={nowPlayingMovies} 
          onMovieClick={handleMovieClick}
        />
      </div>

      <MovieDetailsModal 
        movie={selectedMovie} 
        isOpen={isModalOpen} 
        onClose={() => setIsModalOpen(false)}
      />
    </div>
  );
}