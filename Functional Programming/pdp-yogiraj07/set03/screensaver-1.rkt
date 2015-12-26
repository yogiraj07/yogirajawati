;; The first three lines of this file were inserted by DrRacket. They record metadata
;; about the language level of this file in a form that our tools can easily process.
#reader(lib "htdp-beginner-reader.ss" "lang")((modname screensaver-1) (read-case-sensitive #t) (teachpacks ()) (htdp-settings #(#t constructor repeating-decimal #f #t none #f () #f)))
;;ScreenSaver 1
(require rackunit)
(require "extras.rkt")
(require 2htdp/universe)
(require 2htdp/image)
(provide
  screensaver
  initial-world
  world-after-tick
  world-after-key-event
  world-rect1
  world-rect2
  world-paused?
  new-rectangle
  rect-x
  rect-y
  rect-vx
  rect-vy
 )

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
   

;;DATA DEFINATIONS
(define-struct Rectangle (x y velocity-x velocity-y))
;;A Rectangle is (make-Rectangle NonNegInt NonNegInt Int Int)
;;INTERPRETATION
;;x is the x-coordinate of the center of rectangle, in pixel
;;y is the y-coordinate of the center of rectangle, in pixel
;;velocity-x is the velocity of rectangle in x-direction, in pixels/tick
;;velocity-y is the velocity of rectangle in y-direction, in pixels/tick
;;TEMPLATE
;;Rectangle-fn: Rectangle-> ??

#|
(define (Rectangle r)
  (...(Rectangle-x r)...
   ...(Rectangle-y r)...
   ...(Rectangle-velocity-x r)
   ...(Rectangle-velocity-y r)))
|#
;;examples of Rectangle for testing
(define  Rectangle-at-200-100 (make-Rectangle 200 100 -12 20))
(define  Rectangle-at-200-200 (make-Rectangle 200 200 23 -14))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(define-struct WorldState(rect-1 rect-2 paused?))
;;A WorldState is (make-WorldState Rectangle Rectangle Boolean)
;;INERPRETATION
;;rect-1 represents Rectangle with Velocities (-12,20) in x & y directions
;;rect-2 represents Rectangle with Velocities (23,-14) in x & y directions
;;paused? describes whether or not the simulation is paused

;;TEMPLATE
;;WorldState-fn:WorldState-> ??
#|
(define (WorldState w)
  (...(WorldState-rect-1 w)...
   ...(WorldState-rect-2 w)...
   ...(WorldState-paused? w)))
|#

;;Example of WorldState for Testing
(define intial-WorldState (make-WorldState Rectangle-at-200-100
                                           Rectangle-at-200-200
                                           true))

(define unpaused-intial-WorldState (make-WorldState  Rectangle-at-200-100
                                           Rectangle-at-200-200
                                           false))




;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; screensaver : PosReal -> WorldState
;; GIVEN: the speed of the simulation, in seconds/tick
;; EFFECT: runs the simulation, starting with the initial state as
;; specified in the problem set.
;; RETURNS: the final state of the WorldState
;; DESIGN STRATEGY : Combine Simpler Functions


(define (screensaver tick)
  (big-bang (initial-world 1)
            (on-tick world-after-tick tick)
            (on-key world-after-key-event)
            (on-draw world-to-scene)))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; initial-world : Any -> WorldState
;; GIVEN: any value (ignored)
;; RETURNS: the initial world specified in the problem set
;; EXAMPLE
;; (initial-world 1)=intial-WorldState
;; DESIGN STRATEGY: Combine Simpler Functions
(define (initial-world i) intial-WorldState)

;;TEST
(begin-for-test
 (check-equal? (initial-world 1)intial-WorldState
  "Returns intial WorldState that is to be simulated"))
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; world-after-key-event : WorldState KeyEvent -> WorldState
;; GIVEN : A WorldState w and Keyevent ke
;; RETURNS: the WorldState that should follow the given worldstate
;; after the given keyevent. Here we just consider pause key event
;; EXAMPLES: see tests below
;; STRATEGY: Cases on whether the key is a pause event
(define (world-after-key-event w ke)
  (if (is-pause-key-event? ke)
    (world-with-paused-toggled w)
    w))

;; help function for key event
;; is-pause-key-event? : KeyEvent -> Boolean
;; GIVEN: a KeyEvent ke
;; RETURNS: true iff the KeyEvent represents a pause instruction
(define (is-pause-key-event? ke)
  (string=? ke " "))

;; examples for testing
(define pause-key-event " ")
(define non-pause-key-event "np")

;; world-with-paused-toggled : World -> World
;; RETURNS: a world just like the given one, but with paused? toggled
;; EXAMPLE: See test below
;; DESIGN STRATEGY: Use template for WorldState on w
(define (world-with-paused-toggled w)
        (make-WorldState (WorldState-rect-1 w)
                         (WorldState-rect-2 w)
                         (not (WorldState-paused? w))))

;; for world-after-key-event, we need 4 tests: a paused world, and an
;; unpaused world, and a pause-key-event and a non-pause key event.
;;TESTS
(begin-for-test
  (check-equal?
    (world-after-key-event intial-WorldState pause-key-event)
    unpaused-intial-WorldState
    "after pause key, a paused world should become unpaused")

  (check-equal?
    (world-after-key-event unpaused-intial-WorldState pause-key-event)
    intial-WorldState
    "after pause key, an unpaused world should become paused")

  (check-equal?
    (world-after-key-event intial-WorldState non-pause-key-event)
    intial-WorldState
    "after a non-pause key, a paused world should be unchanged")

  (check-equal?
    (world-after-key-event  unpaused-intial-WorldState non-pause-key-event)
     unpaused-intial-WorldState
    "after a non-pause key, an unpaused world should be unchanged"))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; world-rect1 : WorldState -> Rectangle
;; world-rect2 : WorldState -> Rectangle
;; world-paused? : WorldState -> Boolean
;; GIVEN : A WorldState w
;; RETURNS: the specified attribute of the WorldState
;; NOTE: if these are part of the world struct, you don't need to
;; write any deliverables for these functions.
;; EXAMPLE : See Tests
;; DESIGN STRATEGY : Combine Simpler Functions
(define (world-rect1 w) (WorldState-rect-1 w))
(define (world-rect2 w) (WorldState-rect-2 w))
(define (world-paused? w) (WorldState-paused? w))
;; TESTS
(begin-for-test
 (check-equal? (world-rect1 intial-WorldState) Rectangle-at-200-100 
  "First Rectangle is returned")
 (check-equal? (world-rect2 intial-WorldState) Rectangle-at-200-200 
  "Second Rectangle is returned")
 (check-equal? (world-paused? intial-WorldState) true 
  "Since intial-WorldState is in paused state, the fuction returns True"))
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; new-rectangle : NonNegInt NonNegInt Int Int -> Rectangle
;; GIVEN: 2 non-negative integers x and y, and 2 integers vx and vy
;; RETURNS: a rectangle centered at (x,y), which will travel with
;; velocity (vx, vy).
;; EXAMPLE :
;; (new-rectangle 10 20 -10 30)=(make-Rectangle 10 20 -10 30)
;; DESIGN STRATEGY: Combine Simpler Functions
(define (new-rectangle x y vx vy)
  (make-Rectangle x y vx vy))
;; TESTS:
(begin-for-test
 (check-equal? (new-rectangle 10 20 -10 30)(make-Rectangle 10 20 -10 30)
  "Returns a rectangle with center (10,20) and Velocties (-10,30)
   in X and Y directions"))
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; rect-x : Rectangle -> NonNegInt
;; rect-y : Rectangle -> NonNegInt
;; rect-vx : Rectangle -> Int
;; rect-vy : Rectangle -> Int
;; RETURNS: the coordinates of the center of the rectangle and its
;; velocity in the x- and y- directions.
;; EXAMPLE:
;; (rect-x (make-Rectangle 10 20 -10 30))= 10
;; (rect-y (make-Rectangle 10 20 -10 30))= 20
;; (rect-vx (make-Rectangle 10 20 -10 30))=-10
;; (rect-vy (make-Rectangle 10 20 -10 30))=30
;;  DESIGN STRATEGY: Use template for Rectangle on r

(define (rect-x r) (Rectangle-x r))
(define (rect-y r) (Rectangle-y r))
(define (rect-vx r) (Rectangle-velocity-x r))
(define (rect-vy r) (Rectangle-velocity-y r))

;;TEST:
(begin-for-test
 (check-equal? (rect-x (make-Rectangle 10 20 -10 30)) 10
  "Returns x-coordinate of the center of the rectangle")
 (check-equal? (rect-y (make-Rectangle 10 20 -10 30)) 20
  "Returns y-coordinate of the center of the rectangle")
 (check-equal? (rect-vx (make-Rectangle 10 20 -10 30)) -10
  "Returns velocity of rectangle in x direction")
 (check-equal? (rect-vy (make-Rectangle 10 20 -10 30)) 30
  "Returns velocity of rectangle in y direction"))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; CONSTANTS
;; DRAW-RECTANGLE draws rectangle on canvas
(define DRAW-RECTANGLE (rectangle 60 50 "outline" "blue"))
;; DRAW-TEXT-RECT draws coordinate 
(define DRAW-TEXT-RECT1 (text "(-12,20)" 10 "blue"))
(define DRAW-TEXT-RECT2 (text "(23,-14)" 10 "blue"))


;; dimensions of the canvas
(define CANVAS-WIDTH 400)
(define CANVAS-HEIGHT 300)
(define EMPTY-CANVAS (empty-scene CANVAS-WIDTH CANVAS-HEIGHT))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; world-to-scene : World -> Scene
;; RETURNS: a Scene that portrays the given world.
;; EXAMPLE:  (world-to-scene unpaused-intial-WorldState)=intial-image
;; DESIGN STRATEGY: Use template for WorldState on w

(define  (world-to-scene w)
  (place-images
   (list  (draw-rect DRAW-RECTANGLE (draw-text-blue (world-rect1 w)))
          (draw-rect DRAW-RECTANGLE (draw-text-blue (world-rect2 w))))
   (list  (make-posn (rect-x (WorldState-rect-1 w))
                    (rect-y (WorldState-rect-1 w)))
          (make-posn (rect-x (WorldState-rect-2 w))
                    (rect-y (WorldState-rect-2 w))))
   EMPTY-CANVAS))

;;draw-text-blue:Rectangle -> Image
;;GIVEN : A Rectangle r
;;RETURNS : A text image representing velocities of the Rectangle in blue color
;;EXAMPLE : (-12,20)
(define (draw-text-blue r)
  (text (string-append "(" (number->string (rect-vx r)) ","
                       (number->string (rect-vy r)) ")") 10 "blue"))
;;draw-rect: IMAGE IMAGE ->IMAGE
;;GIVEN : A Rectangle Image and Velocity text Image
;;RETURNS: A Single Image combining Rectangle and text using overlay/align
(define (draw-rect rect text)
  (overlay/align "middle" "middle" rect text))

;; tests

;; an image showing rect1 whose center at (200,100) and rect 2 at (200,200)
;; check this visually to make sure it's what you want
(define intial-image (place-images
                      (list (draw-rect DRAW-RECTANGLE DRAW-TEXT-RECT1)
                            (draw-rect DRAW-RECTANGLE DRAW-TEXT-RECT2))
                      (list (make-posn 200 100)
                            (make-posn 200 200)
                       ) EMPTY-CANVAS))
;; note: these only test whether world-to-scene calls place-image properly.
;; it doesn't check to see whether that's the right image!
;; these are not very good test strings!

(begin-for-test
  (check-equal? 
    (world-to-scene unpaused-intial-WorldState)
    intial-image
    "test of (world-to-scene unpaused-intial-WorldState)")

  (check-equal?
    (world-to-scene intial-WorldState)
    intial-image
    "test of (world-to-scene intial-WorldState)"))
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;;CONSTANT
;; For Testing
(define RECT-1 (new-rectangle 100 50 -12 20))
(define RECT-2-BEFORE-TICK (new-rectangle 10 50 23 -14))
(define RECT-2-AFTER-TICK (new-rectangle 33 36 23 -14))


;; Rectangle width and height
(define RECT-WIDTH 30)
(define RECT-HEIGHT 25)

;; Minimum x and y that rectangle can touch the left boundary
(define XMIN RECT-WIDTH)
(define YMIN RECT-HEIGHT)

;; Maximum x and y that rectangle can touch the right boundary
(define XMAX (- CANVAS-WIDTH RECT-WIDTH ))
(define YMAX (- CANVAS-HEIGHT RECT-HEIGHT))

;; world-after-tick : WorldState -> WorldState
;; GIVEN: A WorldState w
;; RETURNS: A world state w that should follow the given world state
;; after a tick.
;; EXAMPLE: See tests
;; DESIGN STRATEGY : Template for WorldState on w

(define (world-after-tick w)
  (if (world-paused? w)
      w
      (make-WorldState (process-rect (world-rect1 w))
                       (process-rect (world-rect2 w))
                       (world-paused? w))))




;; helper functions
;;process-rect: Rectangle->Retangle
;;GIVEN: A Rectangle r
;;Returns: If Rectangle collides with wall (x,y,vx,vy) are calculated
;;         according to problem requirements, else the recatangle moves
;;         new coordinates of rectangle are x+vx and y+vy
(define (process-rect r)
  (cond
    [(and (collide-left-x? r) (collide-up-y? r)) (new-rectangle XMIN YMIN
                                                           (reverse-vx r)
                                                           (reverse-vy r))]
    [(and (collide-left-x? r) (collide-down-y? r)) (new-rectangle XMIN YMAX
                                                           (reverse-vx r)
                                                           (reverse-vy r))]
    [(and (collide-right-x? r) (collide-up-y? r)) (new-rectangle XMAX YMIN
                                                           (reverse-vx r)
                                                           (reverse-vy r))]
    [(and (collide-right-x? r) (collide-down-y? r)) (new-rectangle XMAX YMAX
                                                           (reverse-vx r)
                                                           (reverse-vy r))]
    [(collide-right-x? r) (new-rectangle XMAX (+ (rect-y r)(rect-vy r))
                          (reverse-vx r) (rect-vy r))]
    [(collide-left-x? r) (new-rectangle XMIN (+ (rect-y r)(rect-vy r))
                          (reverse-vx r) (rect-vy r))]
    [(collide-down-y? r) (new-rectangle (+ (rect-x r) (rect-vx r)) YMAX
                                        (rect-vx r) (reverse-vy r))] 
    [(collide-up-y? r) (new-rectangle (+ (rect-x r) (rect-vx r)) YMIN
                                      (rect-vx r) (reverse-vy r))]
    [else (new-rectangle (next-tick-x (rect-x r) (rect-vx r))
                         (next-tick-y (rect-y r) (rect-vy r))
                         (rect-vx r) (rect-vy r))]
    ))

;;TESTS:
(begin-for-test
  (check-equal? 
    (world-after-tick (make-WorldState RECT-1 RECT-2-BEFORE-TICK true))
    (make-WorldState RECT-1 RECT-2-BEFORE-TICK true)
    "if the WorldState is paused, same WorldState is returned with no changes")
  (check-equal? 
    (world-after-tick (create-world 29 4 -12 20 RECT-2-BEFORE-TICK))
    (create-world 30 25 12 -20 RECT-2-AFTER-TICK)
    "test of rectangle bouncing back from top left corner")
  (check-equal? 
    (world-after-tick (create-world 29 276 -12 20 RECT-2-BEFORE-TICK))
    (create-world 30 275 12 -20 RECT-2-AFTER-TICK)
    "test of rectangle bouncing back from bottom left corner")
  (check-equal? 
    (world-after-tick (create-world 390 275 -12 20 RECT-2-BEFORE-TICK))
    (create-world 370 275 12 -20 RECT-2-AFTER-TICK)
    "test of rectangle bouncing back from bottom right corner")
  (check-equal? 
    (world-after-tick (create-world 390 3 -12 20 RECT-2-BEFORE-TICK))
    (create-world 370 25 12 -20 RECT-2-AFTER-TICK)
    "test of rectangle bouncing back from top right corner")
  (check-equal? 
    (world-after-tick (create-world 390 45 -12 20 RECT-2-BEFORE-TICK))
    (create-world 370 65 12 20 RECT-2-AFTER-TICK)
    "test of rectangle bouncing back from right vertical edge")
  (check-equal? 
    (world-after-tick (create-world 12 45 -12 20 RECT-2-BEFORE-TICK))
    (create-world 30 65 12 20 RECT-2-AFTER-TICK)
    "test of rectangle bouncing back from left vertical edge")
  (check-equal? 
    (world-after-tick (create-world 100 3 -12 20 RECT-2-BEFORE-TICK))
    (create-world 88 25 -12 -20 RECT-2-AFTER-TICK)
    "test of rectangle bouncing back from upper horizontal edge")
  (check-equal? 
    (world-after-tick (create-world 100 276 -12 20 RECT-2-BEFORE-TICK))
    (create-world 88 275 -12 -20 RECT-2-AFTER-TICK)
    "test of rectangle bouncing back from bottom horizontal edge")
  )

;;helper function for testing
;;create-world: PosInt PosInt int int Rectangle->WorldState
;;GIVEN : x,y,vx,vy of Rectangle 1,Reactangle 2
;;RETURNS : A new unpaused WorldState of Rectangle 1,Rectangle 2 
(define (create-world x y vx vy r2)
  (make-WorldState (new-rectangle x y vx vy) r2 false))

;;next-tick-x: Int -> Int
;;GIVEN : x or y coordinate of rectangle, Vx or Vy velocities of the rectangle
;;RETURNS: the new coordinates x,y after next tick
(define (next-tick-x x vx)
  (+ x vx))
(define (next-tick-y y vy)
  (+ y vy))

;; collide-left-x?: Rectangle-> Boolean
;; collide-right-x?: Rectangle-> Boolean
;; collide-up-y?: Rectangle-> Boolean
;; collide-down-y?: Rectangle-> Boolean
;; GIVEN : A Rectangle r
;; RETURNS : Returns true iff rectangle collides at any edge of canvas
(define (collide-left-x? r)
  (<(next-tick-x (rect-x r) (rect-vx r)) XMIN))
(define (collide-right-x? r)
  (>(next-tick-x (rect-x r) (rect-vx r)) XMAX))
(define (collide-up-y? r)
  (<(next-tick-y (rect-y r) (rect-vy r)) YMIN))
(define (collide-down-y? r)
  (>(next-tick-y (rect-y r) (rect-vy r)) YMAX))

;;reverse-vx: Rectangle->Int
;;reverse-vy: Rectangle->Int
;;GIVEN : A rectangle
;;RETURNS : Velocity of the rectangle in opposite direction
(define (reverse-vx r) (- 0 (rect-vx r)))
(define (reverse-vy r) (- 0 (rect-vy r)))

