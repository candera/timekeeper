(ns timekeeper.main
  (:require [dynne.sampled-sound :as dynne])
  (:gen-class)
  (:import [javax.sound.sampled AudioFormat AudioSystem Clip]))

(defn make-sin-clip
  "Returns a clip of sound that's a pure sine wave of frequency `freq`
  Hertz of duration `duration` seconds, with the envelope describe by
  `attack`, `sustain`, and `decay`."
  [freq duration attack sustain decay gain]
  (-> (dynne/sinusoid duration freq)
      (dynne/envelope (dynne/segmented-linear 1
                                              0.0 attack
                                              1.0 sustain
                                              1.0 decay
                                              0.0))
      (dynne/gain gain)))

(defn play
  "Plays a clip"
  [clip]
  (dynne/play clip))

(defn timer
  "Gives an audible cue for a set of `iterations` of on-off cycles.

  First, waits `delay` seconds. Then `iterations` times in a row,
  alternates between on periods of length `on` and `off` periods of
  length `off`."
  [delay on off iterations gain]
  (let [countdown (make-sin-clip 440.0 0.1 0.01 0.08 0.01 gain)
        start (make-sin-clip 660.0 0.5 0.01 0.48 0.01 gain)
        stop (make-sin-clip 330.0 0.5 0.01 0.48 0.01 gain)
        done (mapv #(make-sin-clip % 0.1 0.01 0.08 0.01 gain) [440.0 660.0 440.0
                                                               550.0 450.0 880.0])]
    (Thread/sleep (* 1000 delay))
    (dotimes [i iterations]
      (dotimes [_ 3]
        (play countdown)
        (Thread/sleep 1000))
      (play start)
      (Thread/sleep (* 1000 (- on 3)))
      (dotimes [_ 3]
        (play countdown)
        (Thread/sleep 1000))
      (play stop)
      (when-not (= i (dec iterations))
        (Thread/sleep (* 1000 (- off 3)))))
    (Thread/sleep 1000)
    (doseq [clip done]
      (play clip)
      (Thread/sleep 200))))

(defn db->gain
  "Convert decibels to a power shift"
  [db]
  (as-> db ?
        (/ ? 10)
        (Math/pow 10 ?)))

(defn -main
  [& args]
  (let [[delay on off iterations volume] args]
    (timer (Long/parseLong delay)
           (Long/parseLong on)
           (Long/parseLong off)
           (Long/parseLong iterations)
           (-> volume Long/parseLong db->gain)))
  (shutdown-agents))
