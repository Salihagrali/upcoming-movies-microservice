import { useState, useEffect } from 'react';
import { Play, Info } from 'lucide-react';
import type { MovieData } from './types/movie';
import { Navbar } from './components/common/Navbar';
import { MovieSlider } from './components/movies/MovieSlider';
import { MovieDetailsModal } from './components/movies/MovieDetailsModal';


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
            <div className="absolute inset-0 bg-linear-to-r from-[#141414] via-[#141414]/30 to-transparent" />
            <div className="absolute inset-0 bg-linear-to-t from-[#141414] via-transparent to-transparent" />
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