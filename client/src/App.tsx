import { Suspense, useEffect, lazy } from "react";
import { Routes, Route, Navigate } from "react-router-dom";
import { useAudio } from "./lib/stores/useAudio";
import Home from "./pages/Home";
import NotFound from "./pages/not-found";

// Lazy load game page for better performance
const Game = lazy(() => import("./pages/Game"));

function App() {
  // Setup audio elements
  useEffect(() => {
    // Create audio elements
    const backgroundMusic = new Audio("/sounds/background.mp3");
    backgroundMusic.loop = true;
    backgroundMusic.volume = 0.3;
    
    const hitSound = new Audio("/sounds/hit.mp3");
    const successSound = new Audio("/sounds/success.mp3");
    
    // Set up in our global audio store
    const audioStore = useAudio.getState();
    audioStore.setBackgroundMusic(backgroundMusic);
    audioStore.setHitSound(hitSound);
    audioStore.setSuccessSound(successSound);
    
    // Clean up on unmount
    return () => {
      backgroundMusic.pause();
      hitSound.pause();
      successSound.pause();
    };
  }, []);

  return (
    <Suspense fallback={<div className="loading">Loading...</div>}>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/game/:mode" element={<Game />} />
        <Route path="/404" element={<NotFound />} />
        <Route path="*" element={<Navigate to="/404" replace />} />
      </Routes>
    </Suspense>
  );
}

export default App;
