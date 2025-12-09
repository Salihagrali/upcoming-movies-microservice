import { useEffect, useRef } from "react";
import type { MovieData } from "../../types/movie";
import { MovieCard } from "./MovieCard";

interface MovieSliderProps {
  title: string;
  movies: MovieData[];
  onMovieClick: (movie: MovieData) => void;
}

export const MovieSlider: React.FC<MovieSliderProps> = ({ title, movies, onMovieClick }) => {
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