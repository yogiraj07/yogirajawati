;; The first three lines of this file were inserted by DrRacket. They record metadata
;; about the language level of this file in a form that our tools can easily process.
#reader(lib "htdp-beginner-reader.ss" "lang")((modname probe) (read-case-sensitive #t) (teachpacks ()) (htdp-settings #(#t constructor repeating-decimal #f #t none #f () #f)))
(require rackunit)
(require "extras.rkt")
(provide
   probe-at
   probe-turned-left
   probe-turned-right
   probe-forward
   probe-north?
   probe-south?
   probe-east?
   probe-west?
  )


(define-struct Probe (x y direction))

;;DATA DEFINATION
;;A Probe is a (make-Probe Real Real String)
;;INTERPRETATION:
;;x is the x-coordinate of the Probe
;;y is the y-coordinate of the Probe
;;direction is the direction of the Probe which can be either be north,east,south,west
;;TEMPLATE :
;;Probe-fn : Probe-> ??
#|
(define (Probe-fn p)
 (...
  (Probe-x p)
  (Probe-y p)
  (Probe-direction p)
  ...
 ))  
|#


;;probe-at : Integer Integer -> Probe
;;GIVEN: an x-coordinate and a y-coordinate
;;WHERE: these coordinates leave the robot entirely inside the trap
;;RETURNS: a probe with its center at those coordinates, facing north.
;;EXAMPLE: a set of coordinates that put the robot in contact with the wall is not consistent with the contract.
;;(probe-at 1 2)=(make-Probe x y "north")
;;DESIGN STRATEGY: Combine Simpler Functions

(define (probe-at x y)
  (make-Probe x y "north"))

;;probe-turned-left : Probe -> Probe
;;probe-turned-right : Probe -> Probe
;;GIVEN: a probe
;;RETURNS: a probe like the original but turned 90 degrees either left or right.
;;EXAMPLE:
;;(probe-turned-left (make-Probe 1 2 "north"))=(make-Probe 1 2 "west"))
;;(probe-turned-right (make-Probe 1 2 "north")=(make-Probe 1 2 "east"))
;;DESIGN STRATEGY: Use template for Probe on p

(define (probe-turned-left p)
  (cond
    [(string=?(Probe-direction p) "north" )(make-Probe (Probe-x p) (Probe-y p) "west")]
    [(string=?(Probe-direction p) "west" )(make-Probe (Probe-x p) (Probe-y p) "south")]
    [(string=?(Probe-direction p) "south" )(make-Probe (Probe-x p) (Probe-y p) "east")]
    [(string=?(Probe-direction p) "east" )(make-Probe (Probe-x p) (Probe-y p) "north")]))

(define (probe-turned-right p)
  (cond
    [(string=?(Probe-direction p) "north" )(make-Probe (Probe-x p) (Probe-y p) "east")]
    [(string=?(Probe-direction p) "east" )(make-Probe (Probe-x p) (Probe-y p) "south")]
    [(string=?(Probe-direction p) "south" )(make-Probe (Probe-x p) (Probe-y p) "west")]
    [(string=?(Probe-direction p) "west" )(make-Probe (Probe-x p) (Probe-y p) "north")]))


;;probe-forward : Probe PosInt -> Probe
;;GIVEN: a probe and a distance in cm
;;RETURNS: a probe like the given one, but moved forward by the specified distance.  If moving forward the specified distance would
;;         cause the probe to hit any wall of the trap, then the probe should move as far as it can inside the trap, and then stop.
;;EXAMPLE:
;;(probe-forward (make-Probe 0 20 "north") 5)=(make-Probe 0 15 "north")
;;(probe-forward (make-Probe 0 20 "north") 100)=(make-Probe 0 -80 "north")
;;(probe-forward (make-Probe 0 20 "north") 200)=(make-Probe 0 -126 "south")

;;DESIGN STRATEGY: Use template for Probe on p

(define (probe-forward p dist)
  (cond
    [(string=? (Probe-direction p) "north") (move-north p dist)]
    [(string=? (Probe-direction p) "south") (move-south p dist)]
    [(string=? (Probe-direction p) "east") (move-east p dist)]
    [(string=? (Probe-direction p) "west") (move-west p dist)]))



;;move-north: Probe PosInt -> Probe
;;move-south: Probe PosInt -> Probe
;;move-west:  Probe PosInt -> Probe
;;move-east:  Probe PosInt -> Probe
;;GIVEN: a probe and a distance in cm
;;RETURNS: The probe with new (x,y) co-ordinates, where the probe moves in the initial direction, and if it is going to hit wall, probe
;;         moves farthest from the wall by taking two left turns making total turn of 180 degree (in the reverse direction of the intital one)
;;DESIGN STRATEGY: Combine Simpler Functions


(define (move-north p dist)
  (if (>=(+ (Probe-y p) 153) dist)
   (make-Probe(Probe-x p) (- (Probe-y p) dist) "north")
   (make-Probe(Probe-x p) (- dist (+(Probe-y p) 306)) "south")
   ))
(define (move-west p dist)
  (if (>=(+ (Probe-x p) 153) dist)
   (make-Probe (- (Probe-x p) dist) (Probe-y p) "west")
   (make-Probe (- dist (+(Probe-x p) 306)) (Probe-y p) "east")
   ))

(define (move-south p dist)
  (if (<=(+ (Probe-y p) dist) 153)
   (make-Probe(Probe-x p) (+ (Probe-y p) dist) "south")
   (make-Probe(Probe-x p) (- 306 (+(Probe-y p) dist)) "north")
   ))

(define (move-east p dist)
  (if (<=(+ (Probe-x p) dist) 153)
   (make-Probe (+ (Probe-x p) dist) (Probe-y p) "east")
   (make-Probe (- 306 (+(Probe-x p) dist)) (Probe-y p) "west")
   ))

;;probe-north? : Probe -> Boolean
;;probe-south? : Probe -> Boolean
;;probe-east? : Probe -> Boolean
;;probe-west? : Probe -> Boolean
;;GIVEN: a probe
;;ANSWERS: whether the probe is facing in the specified direction.
;;EXAMPLE:
;;(probe-north? (make-Probe 1 2 "north"))=true
;;(probe-north? (make-Probe 1 2 "south"))=false
;;(probe-east? (make-Probe 1 2 "east"))=true
;;DESIGN STRATEGY: Use template for Probe on p

(define (probe-north? p)
   (string=? (Probe-direction p) "north"))
(define (probe-south? p)
   (string=? (Probe-direction p) "south"))
(define (probe-east? p)
   (string=? (Probe-direction p) "east"))
(define (probe-west? p)
   (string=? (Probe-direction p) "west"))



(begin-for-test
  (check-equal?(probe-at 1 2) (make-Probe 1 2 "north"))
  (check-equal?(probe-turned-left (make-Probe 1 2 "north")) (make-Probe 1 2 "west"))
  (check-equal?(probe-turned-right (make-Probe 1 2 "north"))(make-Probe 1 2 "east"))
  (check-equal?(probe-north? (make-Probe 1 2 "north")) true)
  (check-equal?(probe-west? (make-Probe 1 2 "west")) true)
  (check-equal?(probe-south? (make-Probe 1 2 "south")) true)
  (check-equal?(probe-east? (make-Probe 1 2 "east")) true)
  (check-equal?(probe-east? (make-Probe 1 2 "west")) false)
  (check-equal?(probe-forward (make-Probe 0 20 "north") 5) (make-Probe 0 15 "north"))
  (check-equal?(probe-forward (make-Probe 0 20 "north") 100) (make-Probe 0 -80 "north"))
  (check-equal?(probe-forward (make-Probe 0 20 "north") 200) (make-Probe 0 -126 "south"))
  (check-equal?(probe-forward (make-Probe 0 0 "north") 153) (make-Probe 0 -153 "north"))
  (check-equal?(probe-forward (make-Probe 0 0 "south") 153) (make-Probe 0 153 "south"))
  (check-equal?(probe-forward (make-Probe 0 20 "south") 183) (make-Probe 0 103 "north"))
  (check-equal?(probe-forward (make-Probe 0 -20 "south") 183) (make-Probe 0 143 "north"))
  (check-equal?(probe-forward (make-Probe 20 1 "west") 200) (make-Probe -126 1 "east"))
  (check-equal?(probe-forward (make-Probe 20 20 "west") 100) (make-Probe -80 20 "west"))
  (check-equal?(probe-forward (make-Probe 0 0 "east") 153) (make-Probe 153 0 "east"))
  (check-equal?(probe-forward (make-Probe 20 0 "east") 183) (make-Probe 103 0 "west")))