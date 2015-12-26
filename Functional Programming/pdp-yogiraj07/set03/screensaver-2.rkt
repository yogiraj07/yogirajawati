;; The first three lines of this file were inserted by DrRacket. They record metadata
;; about the language level of this file in a form that our tools can easily process.
#reader(lib "htdp-beginner-reader.ss" "lang")((modname screensaver-2) (read-case-sensitive #t) (teachpacks ()) (htdp-settings #(#t constructor repeating-decimal #f #t none #f () #f)))
;;ScreenSaver 2 with mouse-event
(require rackunit)
(require "extras.rkt")
(require 2htdp/universe)
(require 2htdp/image)
(provide
  screensaver
  initial-world
  world-after-tick
  world-after-key-event
  world-after-mouse-event
  rect-after-mouse-event
  world-rect1
  world-rect2
  world-paused?
  new-rectangle
  rect-x
  rect-y
  rect-vx
  rect-vy
  rect-selected?
 )

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
   

;;DATA DEFINATIONS
(define-struct Rectangle (x y velocity-x velocity-y selected? x-mx-diff
                                                              y-my-diff))
;;A Rectangle is (make-Rectangle NonNegInt NonNegInt Int Int Boolean Int Int)
;;INTERPRETATION
;;x is the x-coordinate of the center of rectangle, in pixel
;;y is the y-coordinate of the center of rectangle, in pixel
;;velocity-x is the velocity of rectangle in x-direction, in pixels/tick
;;velocity-y is the velocity of rectangle in y-direction, in pixels/tick
;;selected? is true iff the rectangle is clicked by the mouse
;;x-mx-diff is the difference between mouse pointer and x coordinate of
;;             center of Rectangle
;;y-my-diff is the difference between mouse pointer and y coordinate of
;;             center of Rectangle

;;TEMPLATE
;;Rectangle-fn: Rectangle-> ??

#|
(define (Rectangle r)
  (...(Rectangle-x r)...
   ...(Rectangle-y r)...
   ...(Rectangle-velocity-x r)
   ...(Rectangle-velocity-y r)
   ...(Rectangle-selected? r)
   ...(Rectangle-x-mx-diff r)
   ...(Rectangle-y-my-diff r))
|#
;;examples of Rectangle for testing
(define  Rectangle-at-200-100 (make-Rectangle 200 100 -12 20 false 0 0))
(define  Rectangle-at-200-200 (make-Rectangle 200 200 23 -14 false 0 0))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(define-struct WorldState(rect-1 rect-2 paused? mx my))
;;A WorldState is (make-WorldState Rectangle Rectangle Boolean int int)
;;INERPRETATION
;;rect-1 represents Rectangle with Velocities (-12,20) in x & y directions
;;rect-2 represents Rectangle with Velocities (23,-14) in x & y directions
;;paused? describes whether or not the simulation is paused
;;mx represents x coordinate of the mouse iff clicked inside the canvas
;;my represents y coordinate of the mouse iff clicked inside the canvas

;;TEMPLATE
;;WorldState-fn:WorldState-> ??
#|
(define (WorldState w)
  (...(WorldState-rect-1 w)...
   ...(WorldState-rect-2 w)...
   ...(WorldState-paused? w)
   ...(WorldState-mx w)
   ...(WorldState-my w)))
|# 

;;Example of WorldState for Testing
(define intial-WorldState (make-WorldState Rectangle-at-200-100
                                           Rectangle-at-200-200
                                           true 0 0))

(define unpaused-intial-WorldState (make-WorldState  Rectangle-at-200-100
                                           Rectangle-at-200-200
                                           false 0 0))




;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; screensaver : PosReal -> WorldState
;; GIVEN: the speed of the simulation, in seconds/tick
;; EFFECT: runs the simulation, starting with the initial state as
;;         specified in the problem set.
;; RETURNS: the final state of the WorldState
;; DESIGN STRATEGY : Combine Simpler Functions
(define (screensaver tick)
  (big-bang (initial-world 1)
            (on-tick world-after-tick tick)
            (on-key world-after-key-event)
            (on-mouse world-after-mouse-event)
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
;;          after the given keyevent.Here we just consider pause key event
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
;; EXAMPLE: See tests below
;; DESIGN STRATEGY: Use template for WorldState on w
(define (world-with-paused-toggled w)
        (make-WorldState (WorldState-rect-1 w)
                         (WorldState-rect-2 w)
                         (not (WorldState-paused? w)) 0 0))

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
;; EXAMPLE: See Tests
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
;; velocity (vx, vy) and the rectangle is unselected.
;; EXAMPLE :
;; (new-rectangle 10 20 -10 30)=(make-Rectangle 10 20 -10 30 false 0 0)
;; DESIGN STRATEGY: Combine Simpler Functions
(define (new-rectangle x y vx vy)
  (make-Rectangle x y vx vy false 0 0))
;; TESTS:
(begin-for-test
 (check-equal? (new-rectangle 10 20 -10 30)(make-Rectangle 10 20 -10 30
                                                              false 0 0)
  "Returns a rectangle with center (10,20) and Velocties (-10,30)
   in X and Y directions"))
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; rect-x : Rectangle -> NonNegInt
;; rect-y : Rectangle -> NonNegInt
;; rect-vx : Rectangle -> Int
;; rect-vy : Rectangle -> Int
;; RETURNS: the coordinates of the center of the Rectangle r and its
;; velocity in the x- and y- directions.
;; EXAMPLE:
;; (rect-x (make-Rectangle 10 20 -10 30 false 0 0))= 10
;; (rect-y (make-Rectangle 10 20 -10 30 false 0 0))= 20
;; (rect-vx (make-Rectangle 10 20 -10 30 false 0 0))=-10
;; (rect-vy (make-Rectangle 10 20 -10 30 false 0 0))=30
;; DESIGN STRATEGY: Use template for Rectangle on r

(define (rect-x r) (Rectangle-x r))
(define (rect-y r) (Rectangle-y r))
(define (rect-vx r) (Rectangle-velocity-x r))
(define (rect-vy r) (Rectangle-velocity-y r))

;;TEST:
(begin-for-test
 (check-equal? (rect-x (make-Rectangle 10 20 -10 30 false 0 0)) 10
  "Returns x-coordinate of the center of the rectangle")
 (check-equal? (rect-y (make-Rectangle 10 20 -10 30 false 0 0)) 20
  "Returns y-coordinate of the center of the rectangle")
 (check-equal? (rect-vx (make-Rectangle 10 20 -10 30 false 0 0)) -10
  "Returns velocity of rectangle in x direction")
 (check-equal? (rect-vy (make-Rectangle 10 20 -10 30 false 0 0)) 30
  "Returns velocity of rectangle in y direction"))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; CONSTANTS
;; DRAW-RECTANGLE draws rectangle on canvas
(define DRAW-RECTANGLE (rectangle 60 50 "outline" "blue"))
(define DRAW-RECTANGLE-BLUE (rectangle 60 50 "outline" "blue"))
(define DRAW-RECTANGLE-RED (rectangle 60 50 "outline" "red"))
;; DRAW-TEXT-RECT draws velocities of the rectangle 
(define DRAW-TEXT-RECT1-BLUE (text "(-12,20)" 10 "blue"))
(define DRAW-TEXT-RECT1-RED (text "(-12,20)" 10 "red"))
(define DRAW-TEXT-RECT2-BLUE (text "(23,-14)" 10 "blue"))
(define DRAW-TEXT-RECT2-RED (text "(23,-14)" 10 "red"))

;; Used to draw circle of radius 5 around the mouse pointer
(define CIRCLE (circle 5 "outline" "red"))
(define WCIRCLE (circle 0 "outline" "white"))

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
  (place-images (list  (if (rect-selected? (world-rect1 w))
                           (draw-rect DRAW-RECTANGLE-RED
                                      (draw-text-red (world-rect1 w)))
                           (draw-rect DRAW-RECTANGLE-BLUE
                                      (draw-text-blue (world-rect1 w))))
                       (if (rect-selected? (world-rect2 w))
                           (draw-rect DRAW-RECTANGLE-RED
                                      (draw-text-red (world-rect2 w)))
                           (draw-rect DRAW-RECTANGLE-BLUE
                                      (draw-text-blue (world-rect2 w))))
                       (if (rect-selected? (world-rect1 w))  CIRCLE WCIRCLE)
                       (if (rect-selected? (world-rect2 w))  CIRCLE WCIRCLE))
                (list (make-posn (rect-x (WorldState-rect-1 w))
                                 (rect-y (WorldState-rect-1 w))) 
                      (make-posn (rect-x (WorldState-rect-2 w))
                                 (rect-y (WorldState-rect-2 w)))
                      (make-posn (WorldState-mx w) (WorldState-my w))
                      (make-posn (WorldState-mx w) (WorldState-my w))
                      )

                EMPTY-CANVAS))

;;draw-text-blue:Rectangle -> IMAGE
;;GIVEN : A Rectangle r
;;RETURNS : A text image representing velocities at center of the Rectangle r
;;         in blue color
;;EXAMPLE : (-12,20)
;;DESIGN STRATEGY : Combine Simpler Functions
(define (draw-text-blue r)
  (text (string-append "(" (number->string (rect-vx r)) ","
                       (number->string (rect-vy r)) ")") 10 "blue"))

;;draw-text-red:Rectangle -> IMAGE
;;GIVEN : A Rectangle r
;;RETURNS : A text image representing velocities at center of the Rectangle r
;;         in red color
;;EXAMPLE : (-12,20)
;;DESIGN STRATEGY : Combine Simpler Functions
(define (draw-text-red r)
  (text (string-append "(" (number->string (rect-vx r)) ","
                       (number->string (rect-vy r)) ")") 10 "red"))


;;draw-rect: IMAGE IMAGE ->IMAGE
;;GIVEN : A Rectangle Image and Velocity text Image
;;RETURNS: A Single Image combining Rectangle and text using overlay/align
;;DESIGN STRATEGY : Combine Simpler Functions
(define (draw-rect rect text)
  (overlay/align "middle" "middle" rect text))

;; TESTS

;; an image showing rect1 whose center at (200,100) and rect 2 at (200,200)
;; check this visually to make sure it's what you want
(define image-unselected (place-images
                      (list (draw-rect DRAW-RECTANGLE-BLUE DRAW-TEXT-RECT1-BLUE)
                            (draw-rect DRAW-RECTANGLE-BLUE DRAW-TEXT-RECT2-BLUE)
                             WCIRCLE
                             WCIRCLE)
                            
                      (list (make-posn 200 100)
                            (make-posn 200 200)
                            (make-posn 0 0)
                            (make-posn 0 0)
                       ) EMPTY-CANVAS))
(define image-selected (place-images
                      (list (draw-rect DRAW-RECTANGLE-RED DRAW-TEXT-RECT1-RED)
                            (draw-rect DRAW-RECTANGLE-RED DRAW-TEXT-RECT2-RED)
                             CIRCLE
                             CIRCLE)
                            
                      (list (make-posn 200 100)
                            (make-posn 200 200)
                            (make-posn 10 10)
                            (make-posn 10 10)
                       ) EMPTY-CANVAS))
;; note: these only test whether world-to-scene calls place-image properly.
;; it doesn't check to see whether that's the right image!
;; these are not very good test strings!


;;CONSTANTS:
;; Used for testing
(define UNSELECTED-WORLD (make-WorldState
                          (make-Rectangle 200 100 -12 20 false 0 0)
                          (make-Rectangle 200 200 23 -14 false 0 0) false 0 0 ))
(define SELECTED-WORLD (make-WorldState
                        (make-Rectangle 200 100 -12 20 true 10 10)
                        (make-Rectangle 200 200 23 -14 true 0 0) false 10 10 ))

(begin-for-test
  (check-equal? 
    (world-to-scene UNSELECTED-WORLD)
    image-unselected
    "test of unselected WorldState")

  (check-equal?
    (world-to-scene SELECTED-WORLD)
    image-selected
    "test of Selected WorldState"))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;;CONSTANT
;; For Testing
(define RECT-1 (make-Rectangle 100 50 -12 20 false 0 0 ))
(define RECT-2-BEFORE-TICK (make-Rectangle 10 50 23 -14 false 0 0))
(define RECT-2-AFTER-TICK (make-Rectangle 33 36 23 -14 false 0 0))


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
;;          after a tick.
;; EXAMPLE: See tests below
;; DESIGN STRATEGY : Template for WorldState on w

(define (world-after-tick w)
  (if (world-paused? w)
      w
      (make-WorldState (if (Rectangle-selected?(world-rect1 w)) (world-rect1 w)
                           (process-rect (world-rect1 w)))
                       (if (Rectangle-selected?(world-rect2 w)) (world-rect2 w)
                           (process-rect (world-rect2 w)))
                       (world-paused? w) (WorldState-mx w) (WorldState-my w))))


;;helper functions
;;process-rect: Rectangle->Retangle
;;GIVEN: A Rectangle r
;;Returns: If Rectangle collides with wall (x,y,vx,vy) are calculated
;;         according to problem requirements, else the recatangle moves
;;         new coordinates of rectangle are x+vx and y+vy


(define (process-rect r)
  (cond
    [(and (collide-left-x? r) (collide-up-y? r)) (make-Rectangle XMIN YMIN
                                                      (reverse-vx r)
                                                      (reverse-vy r)
                                                      (Rectangle-selected? r)
                                                      (Rectangle-x-mx-diff r)
                                                      (Rectangle-y-my-diff r))]
    [(and (collide-left-x? r) (collide-down-y? r)) (make-Rectangle XMIN YMAX
                                                      (reverse-vx r)
                                                      (reverse-vy r)
                                                      (Rectangle-selected? r)
                                                      (Rectangle-x-mx-diff r)
                                                      (Rectangle-y-my-diff r))]
    [(and (collide-right-x? r) (collide-up-y? r)) (make-Rectangle XMAX YMIN
                                                      (reverse-vx r)
                                                      (reverse-vy r)
                                                      (Rectangle-selected? r)
                                                      (Rectangle-x-mx-diff r)
                                                      (Rectangle-y-my-diff r))]
    [(and (collide-right-x? r) (collide-down-y? r)) (make-Rectangle XMAX YMAX
                                                      (reverse-vx r)
                                                      (reverse-vy r)
                                                      (Rectangle-selected? r)
                                                      (Rectangle-x-mx-diff r)
                                                      (Rectangle-y-my-diff r))]
    [(collide-right-x? r) (make-Rectangle XMAX (+ (rect-y r)(rect-vy r))
                          (reverse-vx r) (rect-vy r)
                          (Rectangle-selected? r)
                          (Rectangle-x-mx-diff r)
                          (Rectangle-y-my-diff r))]
    [(collide-left-x? r) (make-Rectangle XMIN (+ (rect-y r)(rect-vy r))
                          (reverse-vx r) (rect-vy r)
                          (Rectangle-selected? r)
                          (Rectangle-x-mx-diff r)
                          (Rectangle-y-my-diff r))]
    [(collide-down-y? r) (make-Rectangle (+ (rect-x r) (rect-vx r)) YMAX
                                        (rect-vx r) (reverse-vy r)
                                        (Rectangle-selected? r)
                                        (Rectangle-x-mx-diff r)
                                        (Rectangle-y-my-diff r))] 
    [(collide-up-y? r) (make-Rectangle (+ (rect-x r) (rect-vx r)) YMIN
                                      (rect-vx r) (reverse-vy r)
                                      (Rectangle-selected? r)
                                      (Rectangle-x-mx-diff r)
                                      (Rectangle-y-my-diff r))]
    [else (make-Rectangle (next-tick-x (rect-x r) (rect-vx r))
                          (next-tick-y (rect-y r) (rect-vy r))
                          (rect-vx r) (rect-vy r)
                          (Rectangle-selected? r)
                          (Rectangle-x-mx-diff r)
                          (Rectangle-y-my-diff r))]))


;;TESTS
(begin-for-test
  (check-equal? 
    (world-after-tick (make-WorldState RECT-1 RECT-2-BEFORE-TICK true 0 0))
    (make-WorldState RECT-1 RECT-2-BEFORE-TICK true 0 0)
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
  (check-equal? 
    (world-after-tick SELECTED-WORLD)
    SELECTED-WORLD
    "If the rectangles are selected, then they are not processed")
  )

;;helper function for testing
;;create-world: PosInt PosInt int int Rectangle->WorldState
;;GIVEN : x,y,vx,vy of Rectangle 1,Reactangle 2
;;RETURNS : A new unpaused WorldState of Rectangle 1,Rectangle 2 
(define (create-world x y vx vy r2)
  (make-WorldState (make-Rectangle x y vx vy false 0 0) r2 false 0 0))

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
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; CONSTANTS
;; Tectangle Half width and Half height
(define HALF-RECT-WIDTH 30)
(define HALF-RECT-HEIGHT 25)
;; world-after-mouse-event : WorldState Integer Integer MouseEvent -> World
;; GIVEN: a WorldState w and a description of a mouse event
;; RETURNS: the WorldState that should follow the given mouse event.
;;          The valid mouse-event can be one of button-down, button-up or drag
;; EXAMPLE : See tests below
;; DESIGN STRATEGY: use template for WorldState on w

(define (world-after-mouse-event w mx my mev)
  (make-WorldState
    (rect-after-mouse-event (world-rect1 w) mx my mev)
    (rect-after-mouse-event (world-rect2 w) mx my mev)
    (world-paused? w) mx my))


(define WORLD-BEFORE-BUTTON-DOWN (make-WorldState
                                  (make-Rectangle 200 190 -12 20 false 0 0)
                                  (make-Rectangle 200 200 23 -14 false 0 0)
                                  false 210 195 ))
(define WORLD-AFTER-BUTTON-DOWN (make-WorldState
                                 (make-Rectangle 200 190 -12 20 true 10 5)
                                 (make-Rectangle 200 200 23 -14 true 10 -5)
                                 false 210 195 ))
(define WORLD-BEFORE-BUTTON-DOWN-OUTSIDE (make-WorldState
                                        (make-Rectangle 200 190 -12 20 true 0 0)
                                        (make-Rectangle 200 200 23 -14 true 0 0)
                                        false 390 12 ))
(define WORLD-BEFORE-BUTTON-DOWN-SECOND-TIME (make-WorldState
                                      (make-Rectangle 200 190 -12 20 true 10 5)
                                      (make-Rectangle 200 200 23 -14 true 10 -5)
                                      false 211 196 ))
(define WORLD-AFTER-DRAG (make-WorldState
                          (make-Rectangle 200 190 -12 20 true 10 5)
                          (make-Rectangle 200 200 23 -14 true 10 -5)
                          false 210 195))
(define WORLD-AFTER-DRAG-OUTSIDE (make-WorldState
                                  (make-Rectangle 200 190 -12 20 false 10 5)
                                  (make-Rectangle 200 200 23 -14 false 10 -5)
                                  false 300 300))
(define WORLD-AFTER-BUTTON-UP (make-WorldState
                               (make-Rectangle 200 190 -12 20 false 0 0)
                               (make-Rectangle 200 200 23 -14 false 0 0)
                               false 210 195))
(define WORLD-AFTER-BUTTON-UP-NOT-SELECTED (make-WorldState
                                       (make-Rectangle 200 190 -12 20 false 0 0)
                                       (make-Rectangle 200 200 23 -14 false 0 0)
                                       false 210 195))


;;TESTS:
(begin-for-test
(check-equal? 
    (world-after-mouse-event WORLD-BEFORE-BUTTON-DOWN 210 195 "button-down")
    WORLD-AFTER-BUTTON-DOWN
    "if mouse-event is button down and inside the rectangle, x-mx-diff and
     y-my-diff are calculated")
(check-equal? 
 (world-after-mouse-event WORLD-BEFORE-BUTTON-DOWN-OUTSIDE 390 12 "button-down")
     WORLD-BEFORE-BUTTON-DOWN-OUTSIDE
    "if mouse-event is button down and outside the rectangle, same rectangle is
     returned and no processing takes place")
(check-equal? 
    (world-after-mouse-event WORLD-BEFORE-BUTTON-DOWN-SECOND-TIME  211 196
                                                             "button-down")
    WORLD-BEFORE-BUTTON-DOWN-SECOND-TIME 
    "if mouse-event is button down and inside the rectangle and button
     down event has already occured, same rectangle is
     returned since rectangle is not moved forward")
(check-equal? 
    (world-after-mouse-event WORLD-AFTER-BUTTON-DOWN  210 195 "drag")
    WORLD-AFTER-DRAG
    "if mouse-event is drag and mouse is inside the rectangle, new x,y
     coordinates of the rectangle are calculated")
 (check-equal? 
    (world-after-mouse-event WORLD-AFTER-DRAG-OUTSIDE  300 300 "drag")
    WORLD-AFTER-DRAG-OUTSIDE
    "if mouse-event is drag and mouse is inside the rectangle, new x,y
     coordinates of the rectangle are calculated")
 (check-equal? 
    (world-after-mouse-event WORLD-AFTER-BUTTON-DOWN  210 195 "button-up")
    WORLD-AFTER-BUTTON-UP
    "if mouse-event is button-up and rectangle is selected, new x,y
     coordinates of the rectangle are calculated")
 (check-equal?  
    (world-after-mouse-event WORLD-AFTER-BUTTON-UP-NOT-SELECTED  210 195
                                                             "button-up")
    WORLD-AFTER-BUTTON-UP-NOT-SELECTED
    "if mouse-event is button up and tectangle is not selected,
     no processing takes place and original rectangle is returned")
(check-equal? 
   (world-after-mouse-event WORLD-AFTER-BUTTON-UP-NOT-SELECTED  210 195 "enter")
    WORLD-AFTER-BUTTON-UP-NOT-SELECTED
    "if mouse-event is enter or leave, then no processing takes place. I")
  )
;; rect-after-mouse-event : Rectangle Integer Integer MouseEvent -> Rectangle
;; GIVEN: A Rectangle r and a description of a mouse event
;; RETURNS: the Rectangle that should follow the given mouse event
;; EXAMPLES:  See Tests
;; DESIGN SRATEGY: Cases on mouse event mev
(define (rect-after-mouse-event r mx my mev) 
  (cond
    [(mouse=? mev "button-down") (rect-after-button-down r mx my)]
    [(mouse=? mev "drag") (rect-after-drag r mx my)]
    [(mouse=? mev "button-up") (rect-after-button-up r mx my)]
    [else r]))


;; helper functions:

;; rect-after-button-down : Rectangle Integer Integer -> Rectangle
;; RETURNS: the Rectangle following a button-down at the given location.
;; DESIGN STRATEGY: Use template for Rectangle on r
(define (rect-after-button-down r x y)
  (if (in-rect? r x y)
      (make-Rectangle (rect-x r) (rect-y r) (rect-vx r) (rect-vy r) true
                                (process-x-diff r x)(process-y-diff r y))
      r))

;;process-x-diff: Rectangle Int->Int
;;process-y-diff: Rectangle Int->Int
;;GIVEN : A Rectangle r and x position of mouse pointer
;;Returns: If initial x-mx-diff of r is 0, then rext-x= x -rext-x

(define (process-x-diff r x)
  (if( = (Rectangle-x-mx-diff r) 0)
     (- x (rect-x r) )
     (Rectangle-x-mx-diff r)))

(define (process-y-diff r y)
  (if (= (Rectangle-y-my-diff r) 0)
      (- y (rect-y r) )
      (Rectangle-y-my-diff r)) )

;; rect-after-drag : Rectangle Integer Integer -> Rectangle
;; GIVEN : A Rectangle r and x and y position of mouse pointer
;; RETURNS: the Rectangle following a drag at the given location 
;; DESIGN STRATEGY: Use template for Rectangle on r
(define (rect-after-drag r x y)
  (if (rect-selected? r) 
      (make-Rectangle (- x (Rectangle-x-mx-diff r))
                      (- y (Rectangle-y-my-diff r)) (rect-vx r) (rect-vy r)
                      true (Rectangle-x-mx-diff r) (Rectangle-y-my-diff r))
      r))

;; rect-after-button-up : Rectangle Integer Integer -> Rectangle
;; RETURNS: the Rectangle following a button-up at the given location
;; STRATEGY: Use template for Rectangle on r
(define (rect-after-button-up r x y)
  (if (rect-selected? r)
      (make-Rectangle (- x (Rectangle-x-mx-diff r))
                      (- y (Rectangle-y-my-diff r)) (rect-vx r) (rect-vy r)
                      false 0 0)
      r))
  

;;rect-selected? : Rectangle->Boolean
;;GIVEN : A Rectangle R
;;EXAMPLE:
;;(rect-selected (make-Reactangle 12 13 45 67 true 0 0))=true
;;(rect-selected (make-Reactangle 12 13 45 67 false 0 0))=true
;;RETURNS: The Boolean value of Rectangle-selected? r
;;DESIGN STRATEGY: Use template for Rectangle on r
(define (rect-selected? r)(Rectangle-selected? r))

;; in-rect? : Rectangle Integer Integer -> Boolean
;; GIVEN : Rectangle r and mouse coordinates x and y
;; RETURNS true iff the given coordinate is inside the bounding box of
;; the given Rectangle.
;; EXAMPLES: see tests
;; STRATEGY: Use template for Rectangle on r
(define (in-rect? r x y)
  (and
    (<= 
      (- (rect-x r) HALF-RECT-WIDTH)
      x
      (+ (rect-x r) HALF-RECT-WIDTH))
    (<= 
      (- (rect-y r) HALF-RECT-HEIGHT)
      y
      (+ (rect-y r) HALF-RECT-HEIGHT))))

