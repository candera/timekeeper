(ns timekeeper.main
  (:gen-class)
  (:import [javax.sound.sampled AudioFormat AudioSystem Clip]))

(defn sin-wav
  "Returns a lazy seq of floats that are a pure sine wave of
  frequency `freq` Hertz of duration `duration` seconds, assuming a
  sample rate of `sample-rate`"
  [freq duration sample-rate]
  (for [i (range (* sample-rate duration))]
    (Math/sin (/ (* i freq 2 Math/PI)
                 sample-rate))))

(defn envelope
  "Returns a sequence of floating point numbers that climbs from 0 to 1
  by `attack` seconds, stays at 1 for `sustain` seconds, goes to zero
  after `decay` seconds, and is infinitely zero after that, assuming a
  sample rate of `sample-rate`."
  [attack sustain decay sample-rate]
  (let [attack-samples (Math/round (* attack sample-rate))
        sustain-samples (Math/round (* sustain sample-rate))
        decay-samples (Math/round (* decay sample-rate))]
    (concat
     (map #(/ % (double attack-samples)) (range attack-samples))
     (repeat sustain-samples 1.0)
     (map #(- 1.0 (/ % (double decay-samples))) (range decay-samples))
     (repeat 0.0))))

(defn to-byte-array
  "Returns `s` converted to a byte array. Assumes `s` is a seq of
  floating points numbers on the range [-1.0 1.0]."
  [s]
  (byte-array (map #(byte (* Byte/MAX_VALUE %)) s)))

(defn make-sin-clip
  "Returns a clip of sound that's a pure sine wave of frequency `freq`
  Hertz of duration `duration` seconds, with the envelope describe by
  `attack`, `sustain`, and `decay`."
  [freq duration attack sustain decay]
  (let [sample-rate 16000
        samples (map *
                     (sin-wav freq duration sample-rate)
                     (envelope attack sustain decay sample-rate))
        buffer (to-byte-array samples)]
    (doto (AudioSystem/getClip)
      (.open (AudioFormat. sample-rate 8 1 true false)
             buffer
             0
             (alength buffer)))))

(defn play
  "Plays a clip"
  [clip]
  (doto clip
    (.setFramePosition 0)
    (.start)))

(defn timer
  "Gives an audible cue for a set of `iterations` of on-off cycles.

  First, waits `delay` seconds. Then `iterations` times in a row,
  alternates between on periods of length `on` and `off` periods of
  length `off`."
  [delay on off iterations]
  (let [countdown (make-sin-clip 440.0 0.1 0.01 0.08 0.01)
        start (make-sin-clip 660.0 0.5 0.01 0.48 0.01)
        stop (make-sin-clip 330.0 0.5 0.01 0.48 0.01)
        done (mapv #(make-sin-clip % 0.1 0.01 0.08 0.01) [440.0 660.0 440.0
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

(defn -main
  [& args]
  (let [[delay on off iterations] args]
   (timer (Long/parseLong delay)
          (Long/parseLong on)
          (Long/parseLong off)
          (Long/parseLong iterations))))