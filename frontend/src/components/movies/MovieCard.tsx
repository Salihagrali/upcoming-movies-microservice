import { useState } from "react";
import type { MovieData } from "../../types/movie";
import { Heart, Play } from "lucide-react";

interface MovieCardProps {
  movie: MovieData;
  onMovieClick: (movie: MovieData) => void;
}

export const MovieCard: React.FC<MovieCardProps> = ({ movie, onMovieClick }) => {
  const [isFavorite, setIsFavorite] = useState(false);

  const handleFavoriteClick = (e: React.MouseEvent) => {
    e.stopPropagation();
    setIsFavorite(!isFavorite);
    // API logic here (POST/DELETE)
  };

  return (
    <div 
      onClick={() => onMovieClick(movie)}
      className="relative group w-[160px] md:w-[220px] shrink-0 cursor-pointer transition-all duration-300 hover:scale-105 hover:z-10 rounded-md overflow-hidden"
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