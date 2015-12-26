;; The first three lines of this file were inserted by DrRacket. They record metadata
;; about the language level of this file in a form that our tools can easily process.
#reader(lib "htdp-beginner-reader.ss" "lang")((modname screensaver-3) (read-case-sensitive #t) (teachpacks ()) (htdp-settings #(#t constructor repeating-decimal #f #t none #f () #f)))
;;ScreenSaver 3- with a list of objects
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
  world-paused?
  new-rectangle
  rect-x
  rect-y
  rect-vx
  rect-vy
  rect-selected?
  world-rects
  rect-after-key-event 
 )

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
   

;;DATA DEFINATIONS
(define-struct rectl (x y velocity-x velocity-y selected? x-mx-diff
                                                          y-my-diff))
;;A Rectangle is (make-rectl NonNegInt NonNegInt Int Int Boolean Int Int)
;;INTERPRETATION
;;x is the x-coordinate of the center of Rectangle, in pixel
;;y is the y-coordinate of the center of Rectangle, in pixel
;;velocity-x is the velocity of Rectangle in x-direction, in pixels/tick
;;velocity-y is the velocity of Rectangle in y-direction, in pixels/tick
;;selected? is true iff the Rectangle is clicked by the mouse
;;x-mx-diff is the difference between mouse pointer and x coordinate of
;;             center of Rectangle in pixel
;;y-my-diff is the difference between mouse pointer and y coordinate of
;;             center of Rectangle in pixel

;;TEMPLATE
;;rectl-fn: Rectangle-> ??

#|
(define (rectl r)
  (...(rectl-x r)...
   ...(rectl-y r)...
   ...(rectl-velocity-x r)
   ...(rectl-velocity-y r)
   ...(rectl-selected? r)
   ...(rectl-x-mx-diff r)
   ...(rectl-y-my-diff r))
|#
;;examples of Rectangle for testing
(define  Rectangle-at-200-100 (make-rectl 200 100 -12 20 false 0 0))
(define  Rectangle-at-200-200 (make-rectl 200 200 23 -14 false 0 0))
;;CONSTANTS
;; dimensions of the canvas
(define CANVAS-WIDTH 400)
(define CANVAS-HEIGHT 300)
(define CANVAS-X-CENTER (/ CANVAS-WIDTH 2))
(define CANVAS-Y-CENTER (/ CANVAS-HEIGHT 2))
(define EMPTY-CANVAS (empty-scene CANVAS-WIDTH CANVAS-HEIGHT)) 

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;;ListOfRectangles
;; A ListOfRectangles is either
;; --empty
;; --(cons Rectangle LOR)

;; lor-fn : LOR -> ??
;; (define (lor-fn lor)
;;   (cond
;;     [(empty? lor) ...]
;;     [else (...
;;             (rectl-fn (first lor))
;;             (lor-fn (rest lor)))]))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(define-struct world-state(lor paused? mx my))
;;A WorldState is (make-world-state LOR Boolean int int)
;;INERPRETATION
;;lor represents ListOfRectangles, passed to the WorldState, initially the list
;;    is empty
;;paused? describes whether or not the simulation is paused
;;mx represents x coordinate of the mouse iff clicked inside the canvas
;;my represents y coordinate of the mouse iff clicked inside the canvas

;;TEMPLATE
;;world-state-fn:world-state-> ??
#|
(define (world-state w)
  (...(world-state-lor w)...
   ...(world-state-paused? w)
   ...(world-state-mx w)
   ...(world-state-my w)))
|# 

;;Example of WorldState for Testing
(define intial-world-state (make-world-state '()  true 0 0))
                                     
(define unpaused-intial-world-state (make-world-state '() false
                                                      0 0))
 
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
;; (initial-world 1)=intial-world-state
;; DESIGN STRATEGY: Combine Simpler Functions
(define (initial-world i) intial-world-state)

;;TEST
(begin-for-test
 (check-equal? (initial-world 1)intial-world-state
  "Returns intial WorldState that is to be simulated"))
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; CONSTANTS for Testing
(define NEW-RECT (make-rectl CANVAS-X-CENTER CANVAS-Y-CENTER 0 0 false
                                  0 0))
(define LIST-RECT-1(list Rectangle-at-200-100))
(define LIST-RECT-NEW-1(list NEW-RECT ))
(define WORLD-After-N-1 (make-world-state LIST-RECT-NEW-1 true 0 0))
(define WORLD-BEFORE-N (make-world-state LIST-RECT-1 false 0 0))
(define LIST-RECT-2 (list NEW-RECT Rectangle-at-200-100 ))
(define WORLD-AFTER-N (make-world-state LIST-RECT-2 false 0 0))

(define NEW-RECT-1 (make-rectl CANVAS-X-CENTER CANVAS-Y-CENTER 0 0 true
                               0 0))
(define LIST-RECT-SELECT (list NEW-RECT-1 ))
(define WORLD-SELECT (make-world-state LIST-RECT-SELECT true 0 0))

(define NEW-RECT-LEFT (make-rectl CANVAS-X-CENTER CANVAS-Y-CENTER -2 0 true
                                  0 0))
(define LIST-RECT-LEFT (list NEW-RECT-LEFT ))
(define WORLD-LEFT (make-world-state  LIST-RECT-LEFT true 0 0))

(define NEW-RECT-RIGHT (make-rectl CANVAS-X-CENTER CANVAS-Y-CENTER 2 0 true
                                  0 0))
(define LIST-RECT-RIGHT (list NEW-RECT-RIGHT ))
(define WORLD-RIGHT (make-world-state  LIST-RECT-RIGHT true 0 0))

(define NEW-RECT-UP (make-rectl CANVAS-X-CENTER CANVAS-Y-CENTER 0 -2 true
                                  0 0))
(define LIST-RECT-UP (list NEW-RECT-UP ))
(define WORLD-UP (make-world-state  LIST-RECT-UP true 0 0))

(define NEW-RECT-DOWN (make-rectl CANVAS-X-CENTER CANVAS-Y-CENTER 0 2 true
                                  0 0))
(define LIST-RECT-DOWN (list NEW-RECT-DOWN ))
(define WORLD-DOWN (make-world-state  LIST-RECT-DOWN true 0 0))


;; world-after-key-event : WorldState KeyEvent -> WorldState
;; GIVEN : A WorldState w and Keyevent ke
;; RETURNS: the WorldState that should follow the given worldstate
;;          after the given keyevent.Here we just consider pause key event
;; EXAMPLES: see tests below
;; STRATEGY: Cases on key event whether it is a pause event, or "n" is pressed
(define (world-after-key-event w ke)
  (cond
    [(is-pause-key-event? ke)(world-with-paused-toggled w)]
    [(string=? ke "n") (add-new-rect w)]
    [else (make-world-state (list-after-keyevent (world-rects w) ke)
                                                   (world-paused? w)
                                                  (world-state-mx w)
                                              (world-state-my w))]))



;; helper functions for key event

;; is-pause-key-event? : KeyEvent -> Boolean
;; GIVEN: a KeyEvent ke
;; RETURNS: true iff the KeyEvent represents a pause instruction
;; DESIGN STRATEGY: combine simpler functions
(define (is-pause-key-event? ke)
  (string=? ke " "))

;;add-new-rect : world-state->WorldState
;;GIVEN : A WorlsState w
;;RETURNS: A new WorldState with addition of new rectangle to the list
;;DESIGN STRATEGY: Use template for WorldState on w
(define (add-new-rect w)
  (make-world-state (add-to-list (world-rects w))  (world-state-paused? w)
                   (world-state-mx w) (world-state-my w)))

;;add-to-list : LOR->LOR
;;GIVEN: List of Rectangles lor
;;RETURNS: A list of rectangles with 1 new rectangle added to list
;;STRATEGY: Use template for ListOfRectangles on lor
(define (add-to-list lor)
  (cond
    [(empty? lor) (cons (new-rectangle CANVAS-X-CENTER CANVAS-Y-CENTER 0 0)'())]
    [else
     (cons (new-rectangle CANVAS-X-CENTER CANVAS-Y-CENTER 0 0) lor )]
    ))

;;list-after-keyevent : LOR KeyEvent -> LOR
;;GIVEN : A List of Rectangles and a KeyEvent
;;RETURNS: A List of Rectangles with each rectangle that should follow keyevent
;;STRATEGY: Use template for ListOfRectangles on lor
(define (list-after-keyevent lor ke)
   (cond
     [(empty? lor) empty]
     [else
      (cons
       (rect-after-key-event (first lor) ke)
       (list-after-keyevent (rest lor) ke))]))


;; rect-after-key-event : Rectangle KeyEvent -> Rectangle
;; RETURNS: the state of the rectangle that should follow the given key event.
;;left decreases velocity in x by 2, right increases vx by 2
;;up decreases vy by 2 and down increases vy by 2 (unit-pixels/tick)
;; EXAMPLE: See Tests below
;; DESIGN STRATEGY : Use cases on Key Event ke
(define (rect-after-key-event r ke)
  (cond 
    [(and (string=? ke "left") (rect-selected? r))  (process-velocity r
                                               (- (rect-vx r) 2) (rect-vy r))] 
    [(and (string=? ke "right") (rect-selected? r)) (process-velocity r
                                               (+ (rect-vx r) 2) (rect-vy r))]  
    [(and(string=? ke "up") (rect-selected? r))     (process-velocity r
                                               (rect-vx r) (- (rect-vy r) 2))]
    [(and (string=? ke "down") (rect-selected? r))  (process-velocity r
                                               (rect-vx r) (+ (rect-vy r) 2))]
    [else r]))

;; examples for testing
(define pause-key-event " ")
(define non-pause-key-event "np")

;;process-velocity: Rectangle Int Int -> Rectangle
;;GIVEN: A Rectangle and vx , vy velocities of Rectangle that should follow
;;       the keyevent
;;RETURNS : A new Rectangle with new velocities
;;DESIGN STRATEGY: Use template for Rectangle on r
(define (process-velocity r vx vy)
  (make-rectl (rect-x r) (rect-y r) vx vy (rect-selected? r)
                                          (rectl-x-mx-diff r)
                                          (rectl-y-my-diff r)))


;; world-with-paused-toggled : World -> World 
;; RETURNS: a world just like the given one, but with paused? toggled
;; EXAMPLE: See tests below
;; DESIGN STRATEGY: Use template for WorldState on w
(define (world-with-paused-toggled w)
        (make-world-state (world-state-lor w) (not (world-state-paused? w))
                                                                      0 0))
                         

;; for world-after-key-event, we need 4 tests: a paused world, and an
;; unpaused world, and a pause-key-event and a non-pause key event.

;;TESTS
(begin-for-test
  (check-equal?
    (world-after-key-event intial-world-state pause-key-event)
    unpaused-intial-world-state
    "after pause key, a paused world should become unpaused")

  (check-equal?
    (world-after-key-event unpaused-intial-world-state pause-key-event)
    intial-world-state
    "after pause key, an unpaused world should become paused")

  (check-equal?
    (world-after-key-event intial-world-state non-pause-key-event)
    intial-world-state
    "after a non-pause key, a paused world should be unchanged")

  (check-equal?
    (world-after-key-event  unpaused-intial-world-state non-pause-key-event)
     unpaused-intial-world-state
    "after a non-pause key, an unpaused world should be unchanged")
  (check-equal?
    (world-after-key-event  WORLD-BEFORE-N "n")
     WORLD-AFTER-N
    "after keyevent n A new Rectangle is added to LOR") 
  (check-equal?
    (world-after-key-event intial-world-state  "n")
     WORLD-After-N-1
    "after keyevent n A new Rectangle is added to empty LOR")
  (check-equal?
    (world-after-key-event WORLD-SELECT  "left")
     WORLD-LEFT
    "after keyevent left vx of the selected rect is decreased by 2")
  (check-equal?
    (world-after-key-event WORLD-SELECT  "right")
     WORLD-RIGHT
    "after keyevent right vx of the selected rect is increased by 2")
  (check-equal?
    (world-after-key-event WORLD-SELECT  "up")
     WORLD-UP
    "after keyevent up vy of the selected rect is decreased by 2")
  (check-equal?
    (world-after-key-event WORLD-SELECT  "down")
     WORLD-DOWN
    "after keyevent down vx of the selected rect is increased by 2")
  (check-equal?
    (world-after-key-event WORLD-After-N-1  "ffv")
     WORLD-After-N-1
    "if the keyevent is other than mentioned above same rectangle is returned")
  )   


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; new-rectangle : NonNegInt NonNegInt Int Int -> Rectangle
;; GIVEN: 2 non-negative integers x and y, and 2 integers vx and vy
;; RETURNS: a Rectangle centered at (x,y), which will travel with
;; velocity (vx, vy) and the Rectangle is unselected.
;; EXAMPLE :
;; (new-rectangle 10 20 -10 30)=(make-rectl 10 20 -10 30 false 0 0)
;; DESIGN STRATEGY: Combine Simpler Functions
(define (new-rectangle x y vx vy)
  (make-rectl x y vx vy false 0 0))

;; TESTS:
(begin-for-test
 (check-equal? (new-rectangle 10 20 -10 30)(make-rectl 10 20 -10 30
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
;; (rect-x (make-rectl 10 20 -10 30 false 0 0))= 10
;; (rect-y (make-rectl 10 20 -10 30 false 0 0))= 20
;; (rect-vx (make-rectl 10 20 -10 30 false 0 0))=-10
;; (rect-vy (make-rectl 10 20 -10 30 false 0 0))=30
;; DESIGN STRATEGY: Use template for Rectangle on r

(define (rect-x r) (rectl-x r))
(define (rect-y r) (rectl-y r))
(define (rect-vx r) (rectl-velocity-x r))
(define (rect-vy r) (rectl-velocity-y r))

;;TEST:
(begin-for-test
 (check-equal? (rect-x (make-rectl 10 20 -10 30 false 0 0)) 10
  "Returns x-coordinate of the center of the rectangle")
 (check-equal? (rect-y (make-rectl 10 20 -10 30 false 0 0)) 20
  "Returns y-coordinate of the center of the rectangle")
 (check-equal? (rect-vx (make-rectl 10 20 -10 30 false 0 0)) -10
  "Returns velocity of rectangle in x direction")
 (check-equal? (rect-vy (make-rectl 10 20 -10 30 false 0 0)) 30
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

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; world-to-scene : World -> Scene
;; RETURNS: a Scene that portrays the given world.
;; EXAMPLE:  (world-to-scene unpaused-intial-world-state)=intial-image
;; DESIGN STRATEGY: Use template for WorldState on w

(define  (world-to-scene w)
  (place-images (draw-rect-list (world-rects w))
                (draw-posn-list (world-rects w) (world-state-mx w)
                                                (world-state-my w))
                (draw-canvas w))) 

;;helper functions

;;draw-rect-list: LOR->List of IMAGES
;;GIVEN: List of Rectangles
;;RETURNS: List of Images of those Rectangles
;;DESIGN STRATEGY: Use template for ListOfRectangles on lor

(define (draw-rect-list lor)
  (cond
    [(empty? lor) empty]
    [else
     (cons
       (if(rect-selected? (first lor))
         (draw-rect DRAW-RECTANGLE-RED (draw-text-red (first lor)))
         (draw-rect DRAW-RECTANGLE-BLUE (draw-text-blue (first lor)))
        )
       (draw-rect-list(rest lor)))]
       ))

;;draw-posn-list : LOR int int-> List
;;GIVEN: List of Rectangles,mx,my
;;RETURNS: position list of images for placing on canvas
;;STRATEGY: Use template for ListOfRectangles on lor

(define (draw-posn-list lor mx my)
  (cond
    [(empty? lor) empty]
    [else
     (cons
      (make-posn (rect-x (first lor))(rect-y (first lor)))
      (draw-posn-list(rest lor) mx my))]))

;;draw-canvas: world-state->CANVAS
;;GIVEN: WorldState w
;;RETURNS: if any rect is selected, Red circle is drawn on the canvas
;;         else EMPTY CANVAS is returned
;;DESIGN STRATEGY: Use template for WorldState on w

(define (draw-canvas w)
  (if(any-rect-selected? (world-rects w))
     (place-image CIRCLE (world-state-mx w) (world-state-my w) EMPTY-CANVAS)
     EMPTY-CANVAS))

;;any-rect-selected?-> LOR->Boolean
;;GIVEN:List of Rectangles lor
;;RETURNS: True iff any rectangle in the list is selected
;;STRATEGY: Use template for ListOfRectangles on lor
(define (any-rect-selected? lor)
  (cond
    [(empty? lor) false]
    [else
      (if(rect-selected? (first lor))
           true
          (any-rect-selected? (rest lor)))]))

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

;; note: these only test whether world-to-scene calls place-image properly.
;; it doesn't check to see whether that's the right image!
;; these are not very good test strings!


;;CONSTANTS:
;; Used for testing
(define image-unselected (place-images
                      (list(draw-rect DRAW-RECTANGLE-BLUE DRAW-TEXT-RECT1-BLUE)
                           (draw-rect DRAW-RECTANGLE-BLUE DRAW-TEXT-RECT2-BLUE))
                      (list (make-posn 200 100)
                            (make-posn 200 200))
                       EMPTY-CANVAS))
(define image-selected (place-images
                      (list (draw-rect DRAW-RECTANGLE-RED DRAW-TEXT-RECT1-RED)
                            (draw-rect DRAW-RECTANGLE-RED DRAW-TEXT-RECT2-RED))
                         
                      (list (make-posn 200 100)
                            (make-posn 200 200))             
                      (place-image CIRCLE 10 10 EMPTY-CANVAS)))

(define UNSELECTED-WORLD (make-world-state
                         (list (make-rectl 200 100 -12 20 false 0 0)
                         (make-rectl 200 200 23 -14 false 0 0)) false 0 0 ))
(define SELECTED-WORLD (make-world-state
                        (list(make-rectl 200 100 -12 20 true 10 10)
                        (make-rectl 200 200 23 -14 true 0 0)) false 10 10 ))

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
(define RECT-1 (make-rectl 100 50 -12 20 false 0 0 ))
(define RECT-2-BEFORE-TICK (make-rectl 10 50 23 -14 false 0 0))
(define RECT-2-AFTER-TICK (make-rectl 33 36 23 -14 false 0 0))
 

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
;; DESIGN STRATEGY : use template for WorldState on w

(define (world-after-tick w)
  (if (world-paused? w)
      w
      (make-world-state (process-rect-list (world-rects w))
                        (world-paused? w) (world-state-mx w)
                        (world-state-my w))))


;;helper functions
;;world-paused?: Boolean->Boolean
;;RETURNS: The boolean  value of world-state-paused?
;;DESIGN STRATEGY: Use template for WorldState on w
(define (world-paused? w) (world-state-paused? w))

;;process-rect-list: LOR->LOR
;;GIVEN: A List of Rectangles
;;RETURNS: A List of Rectangles with each Rectangle following tick
;;         as per the description
;;STRATEGY: Use template for ListOfRectangles on lor
(define (process-rect-list lor)
  (cond
    [(empty? lor) empty]
    [else
     (cons
      (if (rect-selected?(first lor))
          (first lor)
          (process-rect (first lor)))
      (process-rect-list (rest lor)))]))



;;process-rect: Rectangle->Retangle
;;GIVEN: A Rectangle r
;;Returns: If Rectangle collides with wall (x,y,vx,vy) are calculated
;;         according to problem requirements, else the recatangle moves
;;         new coordinates of rectangle are x+vx and y+vy
;;DESIGN STRATEGY: Use cases on Rectangle r


(define (process-rect r)
  (cond
    [(and (collide-x? r) (collide-y? r)) (process-new-rect r)]
    [(collide-x? r)(process-x-rect r)]
    [(collide-y? r)(process-y-rect r)]
    [else          (new-rect-no-collide r)]))

;;TESTS 
(begin-for-test
  (check-equal? 
  (world-after-tick (make-world-state (list RECT-1 RECT-2-BEFORE-TICK)
                                      true 0 0))
  (make-world-state (list RECT-1 RECT-2-BEFORE-TICK) true 0 0)
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
;;GIVEN : x,y,vx,vy of Rectangle 1 and Reactangle 2
;;RETURNS : A new unpaused WorldState of Rectangle 1,Rectangle 2
;;DESIGN STRATEGY: Combine Simpler Functions
(define (create-world x y vx vy r2)
   (make-world-state (list (make-rectl x y vx vy false 0 0) r2) false 0 0))

;;next-tick-x: Int -> Int
;;GIVEN : x or y coordinate of rectangle, Vx or Vy velocities of the rectangle
;;RETURNS: the new coordinates x,y after next tick
;;DESIGN STRATEGY: Combine Simpler Functions
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
;; STRATEGY: Combine Simpler Functions
(define (collide-left-x? r)
  (<(next-tick-x (rect-x r) (rect-vx r)) XMIN))
(define (collide-right-x? r)
  (>(next-tick-x (rect-x r) (rect-vx r)) XMAX))
(define (collide-up-y? r)
  (<(next-tick-y (rect-y r) (rect-vy r)) YMIN))
(define (collide-down-y? r)
  (>(next-tick-y (rect-y r) (rect-vy r)) YMAX))

;;collide-x?: Rectangle->Boolean
;;GIVEN: A Rectangle r
;;RETURNS: True iff rectangle is going to collide on x or y axis
;;DESIGN STRATEGY: Use template for Rectangle on r
(define (collide-x? r)
  (or (<(next-tick-x (rect-x r) (rect-vx r)) XMIN)
      (>(next-tick-x (rect-x r) (rect-vx r)) XMAX)))
(define (collide-y? r)
  (or (<(next-tick-y (rect-y r) (rect-vy r)) YMIN)
      (>(next-tick-y (rect-y r) (rect-vy r)) YMAX)))

;;process-new-rect: Rectangle->Rectangle
;;GIVEN : A Rectangle r
;;RETURNS: A Rectangle that follows boundary conditions described in problem
;;DESIGN STRATEGY: cases on x and y coordinate of  Rectangle r
(define (process-new-rect r)
  (cond
    [(and (collide-left-x? r) (collide-up-y? r))    (new-x-y-rect r XMIN YMIN)]
    [(and (collide-left-x? r) (collide-down-y? r))  (new-x-y-rect r XMIN YMAX)]
    [(and (collide-right-x? r) (collide-up-y? r))   (new-x-y-rect r XMAX YMIN)]
    [(and (collide-right-x? r) (collide-down-y? r)) (new-x-y-rect r XMAX YMAX)]
    ))

;;new-x-y-rect:Rectangle->Rectangle
;;GIVEN: A Rectangle r that has touchedone of the corner of the canvas
;;RETURNS: A new Rectangle with new (x,y) and (vx,vy)
;;DESIGN STRATEGY: combine simple functions
(define (new-x-y-rect r x y)
  (make-rectl x y (reverse-vx r) (reverse-vy r)  (rectl-selected? r)
                                                 (rectl-x-mx-diff r)
                                                 (rectl-y-my-diff r)))

;;process-x-rect:Rectangle->Rectangle
;;process-y-rect:Rectangle->Rectangle
;;GIVEN: A Rectangle r
;;RETURNS: A rectangle that follows collision on x and y axis of the canvas
;;DESIGN STRATEGY : Use cases on x and y coordinates of Rectangle r
(define (process-x-rect r)
  (cond
    [(collide-right-x? r) (make-rectl XMAX (+ (rect-y r)(rect-vy r))
                          (reverse-vx r) (rect-vy r)
                          (rectl-selected? r)
                          (rectl-x-mx-diff r)
                          (rectl-y-my-diff r))]
    [(collide-left-x? r) (make-rectl XMIN (+ (rect-y r)(rect-vy r))
                          (reverse-vx r) (rect-vy r)
                          (rectl-selected? r)
                          (rectl-x-mx-diff r)
                          (rectl-y-my-diff r))]))
(define (process-y-rect r)
  (cond
    [(collide-down-y? r) (make-rectl (+ (rect-x r) (rect-vx r)) YMAX
                                        (rect-vx r) (reverse-vy r)
                                        (rectl-selected? r)
                                        (rectl-x-mx-diff r)
                                        (rectl-y-my-diff r))] 
    [(collide-up-y? r) (make-rectl (+ (rect-x r) (rect-vx r)) YMIN
                                      (rect-vx r) (reverse-vy r)
                                      (rectl-selected? r)
                                      (rectl-x-mx-diff r)
                                      (rectl-y-my-diff r))]))

;;new-rect-no-collide: Rectangle->Rectangle
;;GIVEN: A Rectangle which doesn't collide any boundary
;;RETURNS: A Rectangle whose new coordinates are (x+vx,y+vy)
;;DESIGN STRATEGY:
(define (new-rect-no-collide r)
  (make-rectl (next-tick-x (rect-x r) (rect-vx r))
                           (next-tick-y (rect-y r) (rect-vy r))
                           (rect-vx r) (rect-vy r) (rectl-selected? r)
                           (rectl-x-mx-diff r)     (rectl-y-my-diff r)))
;;reverse-vx: Rectangle->Int
;;reverse-vy: Rectangle->Int
;;GIVEN : A rectangle
;;RETURNS : Velocity of the rectangle in opposite direction
;;DESIGN STRATEGY: Use template for Rectangle on r
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
  (make-world-state
    (process-mouse-events-on-list (world-rects w) mx my mev)
    (world-paused? w) mx my))

;;process-mouse-events-on-list: LOR int int String->LOR
;;GIVEN: List of Rectangles, mouse coordinates (mx ,my), mouse event
;;RETURNS : List of Rectangles with each rectangle following mouse event
;;          Mouse-Event is one of the button down,button up and drag
;;STRATEGY: Use template for ListOfRectangles on lor

(define (process-mouse-events-on-list lor mx my mev)
  (
   cond
    [(empty? lor) empty]
    [else
     (cons
      (rect-after-mouse-event (first lor) mx my mev)
      (process-mouse-events-on-list (rest lor) mx my mev)
      )]
   ))

;; rect-after-mouse-event : Rectangle Integer Integer MouseEvent -> Rectangle
;; GIVEN: A Rectangle r and a description of a mouse event
;; RETURNS: the Rectangle that should follow the given mouse event
;; EXAMPLES:  See Tests
;; DESIGN STRATEGY: Cases on mouse event mev
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
      (make-rectl (rect-x r) (rect-y r) (rect-vx r) (rect-vy r) true
                                (process-x-diff r x)(process-y-diff r y))
      r))

;;process-x-diff: Rectangle Int->Int
;;process-y-diff: Rectangle Int->Int
;;GIVEN : A Rectangle r and x position of mouse pointer
;;Returns: If initial x-mx-diff of r is 0, then rext-x= x -rext-x
;;DESIGN STRATEGY: Use template for Rectangle on r

(define (process-x-diff r x)
  (if( = (rectl-x-mx-diff r) 0)
     (- x (rect-x r) )
     (rectl-x-mx-diff r)))

(define (process-y-diff r y)
  (if (= (rectl-y-my-diff r) 0)
      (- y (rect-y r) )
      (rectl-y-my-diff r)) )

;; rect-after-drag : Rectangle Integer Integer -> Rectangle
;; GIVEN : A Rectangle r and x and y position of mouse pointer
;; RETURNS: the Rectangle following a drag at the given location 
;; DESIGN STRATEGY: Use template for Rectangle on r
(define (rect-after-drag r x y)
  (if (rect-selected? r) 
      (make-rectl (- x (rectl-x-mx-diff r))
                      (- y (rectl-y-my-diff r)) (rect-vx r) (rect-vy r)
                      true (rectl-x-mx-diff r) (rectl-y-my-diff r))
      r))

;; rect-after-button-up : Rectangle Integer Integer -> Rectangle
;; RETURNS: the Rectangle following a button-up at the given location
;; STRATEGY: Use template for Rectangle on r
(define (rect-after-button-up r x y)
  (if (rect-selected? r)
      (make-rectl (- x (rectl-x-mx-diff r))
                      (- y (rectl-y-my-diff r)) (rect-vx r) (rect-vy r)
                      false 0 0)
      r))
  


;;CONSTANTS for Testing

(define WORLD-BEFORE-BUTTON-DOWN (make-world-state
                                 (list (make-rectl 200 190 -12 20 false 0 0)
                                  (make-rectl 200 200 23 -14 false 0 0))
                                  false 210 195 )) 
(define WORLD-AFTER-BUTTON-DOWN (make-world-state
                                 (list (make-rectl 200 190 -12 20 true 10 5)
                                 (make-rectl 200 200 23 -14 true 10 -5))
                                 false 210 195 ))

(define WORLD-BEFORE-BUTTON-DOWN-OUTSIDE (make-world-state
                                  (list (make-rectl 200 190 -12 20 true 0 0)
                                       (make-rectl 200 200 23 -14 true 0 0))
                                        false 390 12 ))
(define WORLD-BEFORE-BUTTON-DOWN-SECOND-TIME (make-world-state
                                 (list (make-rectl 200 190 -12 20 true 10 5)
                                     (make-rectl 200 200 23 -14 true 10 -5))
                                      false 211 196 ))
(define WORLD-AFTER-DRAG (make-world-state
                          (list (make-rectl 200 190 -12 20 true 10 5)
                          (make-rectl 200 200 23 -14 true 10 -5))
                          false 210 195))
(define WORLD-AFTER-DRAG-OUTSIDE (make-world-state
                               (list (make-rectl 200 190 -12 20 false 10 5)
                                  (make-rectl 200 200 23 -14 false 10 -5))
                                  false 300 300))
(define WORLD-AFTER-BUTTON-UP (make-world-state
                             (list (make-rectl 200 190 -12 20 false 0 0)
                               (make-rectl 200 200 23 -14 false 0 0))
                               false 210 195))
(define WORLD-AFTER-BUTTON-UP-NOT-SELECTED (make-world-state
                               (list (make-rectl 200 190 -12 20 false 0 0)
                                      (make-rectl 200 200 23 -14 false 0 0))
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


;;rect-selected? : Rectangle->Boolean
;;GIVEN : A Rectangle R
;;EXAMPLE:
;;(rect-selected (make-Reactangle 12 13 45 67 true 0 0))=true
;;(rect-selected (make-Reactangle 12 13 45 67 false 0 0))=true
;;RETURNS: The Boolean value of rectl-selected? r
;;DESIGN STRATEGY: Use template for Rectangle on r
(define (rect-selected? r)(rectl-selected? r))

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

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; world-rects : WorldState -> ListOfRectangle
;; RETURNS: the specified attribute of the WorldState
;; STRATEGY: Use template for WorldState on w
(define (world-rects w)
  (world-state-lor w))


;;  (screensaver 1)