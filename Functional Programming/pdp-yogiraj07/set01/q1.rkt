;; The first three lines of this file were inserted by DrRacket. They record metadata
;; about the language level of this file in a form that our tools can easily process.
#reader(lib "htdp-beginner-reader.ss" "lang")((modname q1) (read-case-sensitive #t) (teachpacks ()) (htdp-settings #(#t constructor repeating-decimal #f #t none #f () #f)))
;;;Problem 13 - To calculate distance of Point (p,q) from the origin
(require rackunit)
(require "extras.rkt")
(provide distance-to-origin)
;no data defination required

;;distance-to-origin: Real->Real
;;GIVEN : The co-ordinates (p,q) of a point
;;RETURNS : The distance of the point (p,q) from the origin by using formula :- sqrt(x^2 + y^2)
;;EXAMPLES:
;(distance-to-origin 3 4)=5
;(distance-to-origin 5 12)=13
;; DESIGN STRATEGY : Combine Simpler Functions

(define (distance-to-origin p q)
  (sqrt(+ (* p p) (* q q))))

;; TESTS
(begin-for-test
  (check-equal? (distance-to-origin 3 4) 5 
    "Distance of point (3,4) from Origin is 5")
  (check-equal? (distance-to-origin 5 12) 13 
    "Distance of point (5,12) from Origin is 13"))
