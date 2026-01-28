import { Heart, Play, X } from "lucide-react";
import type { MovieData } from "../../types/movie";

// 1. UPDATED PROPS: Assume MovieData includes 'isFavorite' and add the toggle handler.
interface ModalProps {
    movie: MovieData & { isFavorite: boolean } | null; // Assume isFavorite is present
    isOpen: boolean;
    onClose: () => void;
    // NEW PROP: Handler to toggle favorite status
    onFavoriteToggle: (movie: MovieData, isFavorite: boolean) => void;
}

export const MovieDetailsModal: React.FC<ModalProps> = ({ movie, isOpen, onClose, onFavoriteToggle }) => {
    // We can now safely assume 'movie.isFavorite' exists when movie is not null
    if (!isOpen || !movie) return null;

    // Determine button appearance and action based on current status
    const isCurrentlyFavorite = movie.isFavorite;

    const handleFavoriteToggle = (e: React.MouseEvent) => {
        e.stopPropagation();
        // Call the parent handler, passing the movie and its current status
        onFavoriteToggle(movie, isCurrentlyFavorite);
    };

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
                        // Note: Added check for truthiness before includes, though likely not needed with your data structure
                        src={movie.backdrop_path && movie.backdrop_path.includes('http') ? movie.backdrop_path : `https://image.tmdb.org/t/p/original${movie.backdrop_path}`} 
                        alt={movie.title} 
                        className="w-full h-full object-cover"
                    />
                    <div className="absolute bottom-10 left-8 z-20 max-w-lg">
                        <h2 className="text-4xl md:text-5xl font-bold mb-4">{movie.title}</h2>
                        <div className="flex gap-3">
                            <button className="bg-white text-black px-8 py-2 rounded font-bold hover:bg-opacity-80 flex items-center gap-2">
                                <Play size={20} className="fill-black" /> Play
                            </button>
                            
                            {/* 3. DYNAMIC BUTTON */}
                            <button 
                                onClick={handleFavoriteToggle} 
                                className={`px-8 py-2 rounded font-bold flex items-center gap-2 transition-colors ${
                                    isCurrentlyFavorite 
                                        ? 'bg-red-600 hover:bg-red-700 text-white' // Favorited style
                                        : 'bg-gray-500/40 hover:bg-gray-500/60 text-white' // Not favorited style
                                }`}
                                title={isCurrentlyFavorite ? "Remove from My List" : "Add to My List"}
                            >
                                <Heart size={20} fill={isCurrentlyFavorite ? "white" : "transparent"} /> 
                                {isCurrentlyFavorite ? "Added" : "My List"}
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
                        {/* NOTE: Genres is still hardcoded here; you'll need to fetch and map genre names if they aren't on MovieData */}
                        <div><span className="text-gray-500">Genres:</span> Action, Thriller</div> 
                        <div><span className="text-gray-500">Original Language:</span> {movie.original_language.toUpperCase()}</div>
                        <div><span className="text-gray-500">Total Votes:</span> {movie.vote_count}</div>
                    </div>
                </div>
            </div>
        </div>
    );
};