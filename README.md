# timekeeper

A Clojure application that will beep to indicate a series of on-off
cycles. I use it to time exercises - 30 seconds on, 20 seconds off,
five repeats. That sort of thing.

## Usage

`lein run <<delay>> <<on>> <<off>> <<iterations>>`

* **delay** : The number of seconds to wait before starting.
* **on** : The number of seconds for each "on" phase.
* **off** : The number of seconds for each "off" phase.
* **iterations** : How many times to run through the on/off phases.

A series of three brief tones will preceed each transition, with a
slightly longer tone indicating the start of each phase. The tone for
the start of the on phase is higher than the tone for the start of
the off phase. A series of six tones indicates the end of the last
iteration.

Listen to it once - you'll get it.

Note that the final "off" phase is always skipped.

## Example

`lein run 2 30 10 3`

Waits two seconds, then does three repeats of 30 seconds on and 10
seconds off.

## License

Copyright © 2013 Craig Andera

Distributed under the Eclipse Public License, the same as Clojure.
