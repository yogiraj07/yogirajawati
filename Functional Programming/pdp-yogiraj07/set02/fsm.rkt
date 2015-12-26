;; The first three lines of this file were inserted by DrRacket. They record metadata
;; about the language level of this file in a form that our tools can easily process.
#reader(lib "htdp-beginner-reader.ss" "lang")((modname fsm) (read-case-sensitive #t) (teachpacks ()) (htdp-settings #(#t constructor repeating-decimal #f #t none #f () #f)))
(require rackunit)
(require "extras.rkt")
(require 2htdp/universe)
(provide
 initial-state
 next-state
 accepting-state?
 error-state?)

;;DATA DEFINATION:
;;A State is one of
;; --"i"
;; --"m"
;; --"final"
;; --"e"

;;INTERPRETATION:
;;  A state 's' represents following one of the state of FSM 
;; "i" is the initial state of the FSM
;; "m" is the intermediate state of the FSM
;; "final" is the Acceptable state of the FSM
;; "e" is the error state of the FSM

;;TEMPLATE
;;state-fn : state-> ??
#|
(define (state-fn state)
 (cond
   [(string=? state "i") ...]
   [(string=? state "m") ...]
   [(string=? state "final") ...]
   [(string=? state "e") ...]))  
|#


;;DATA DEFINATION:
;;A MachineInput is one of
;; --"a"
;; --"b"
;; --"c"
;; --"d"
;; --"e"
;; --"f"


;;TEMPLATE
;;MachineInput-fn : MacInput-> ??
#|
(define (MachineInput-fn MacInput->)
 (cond
   [(string=? MacInput "a") ...]
   [(string=? MacInput "b") ...]
   [(string=? MacInput "c") ...]
   [(string=? MacInput "d") ...]
   [(string=? MacInput "e") ...]
   [(string=? MacInput "f") ...]))  
|#  

;;initial-state : Number -> State
;;GIVEN: a number
;;RETURNS: a representation of the initial state
;;of your machine.  The given number is ignored.
;;EXAMPLES:
;;(initial-state 1)=i
;;STRATEGY: Use combine simpler functions

(define (initial-state num) "i")


;;next-state : State MachineInput -> State
;;GIVEN: a state of the machine and a machine input
;;RETURNS: the state that should follow the given input.
;;EXAMPLES:
;;(next-state ("i" "a"))=i
;;(next-state ("i" "c"))=m
;;(next-state ("m" "c"))=e
;;(next-state ("m" "d"))=final
;;(next-state ("final" "e"))=final
;;(next-state ("e" "a"))=e
;;(next-state ("m" "b"))=m
;; STRATEGY: Use Template for State on s

(define (next-state s MacInput)
  (cond
    [(and(string=? s "i") (string=? MacInput "a")) "i"]
    [(and(string=? s "i") (string=? MacInput "b")) "i"]
    [(and(string=? s "i") (string=? MacInput "c")) "m"]
    [(and(string=? s "i") (string=? MacInput "d")) "e"]
    [(and(string=? s "i") (string=? MacInput "e")) "e"]
    [(and(string=? s "i") (string=? MacInput "f")) "e"]
    [(and(string=? s "m") (string=? MacInput "a")) "m"]
    [(and(string=? s "m") (string=? MacInput "b")) "m"]
    [(and(string=? s "m") (string=? MacInput "c")) "e"]
    [(and(string=? s "m") (string=? MacInput "d")) "final"]
    [(and(string=? s "m") (string=? MacInput "e")) "e"]
    [(and(string=? s "m") (string=? MacInput "f")) "e"]
    [(and(string=? s "final") (string=? MacInput "a")) "e"]
    [(and(string=? s "final") (string=? MacInput "b")) "e"]
    [(and(string=? s "final") (string=? MacInput "c")) "e"]
    [(and(string=? s "final") (string=? MacInput "d")) "e"]
    [(and(string=? s "final") (string=? MacInput "e")) "final"]
    [(and(string=? s "final") (string=? MacInput "f")) "final"]
    [(and(string=? s "e") (string=? MacInput "a")) "e"]
    [(and(string=? s "e") (string=? MacInput "b")) "e"]
    [(and(string=? s "e") (string=? MacInput "c")) "e"]
    [(and(string=? s "e") (string=? MacInput "d")) "e"]
    [(and(string=? s "e") (string=? MacInput "e")) "e"]
    [(and(string=? s "e") (string=? MacInput "f")) "e"]))


;;accepting-state? : State -> Boolean
;;GIVEN: a state of the machine
;;RETURNS: true iff the given state is a final (accepting) state

;;EXAMPLES:
;;(accepting-state? "i")=false
;;(accepting-state? "m")=false
;;(accepting-state? "final")=true
;;(accepting-state? "e")=false

;;STRATEGY: Use template for State on s

(define (accepting-state? s)
 (cond
   [(string=? s "i") false]
   [(string=? s "m") false]
   [(string=? s "final") true]
   [(string=? s "e") false]))  

;;error-state? : State -> Boolean
;;GIVEN: a state of the machine
;;RETURNS: true iff there is no path (empty or non-empty) from the given
;;state to an accepting state

;;EXAMPLES:
;;(error-state? "i")=false
;;(error-state? "m")=false
;;(error-state? "f")=false
;;(error-state? "e")=true

;;STRATEGY: Use template for State on s
(define (error-state? s)
 (cond
   [(string=? s "i") false]
   [(string=? s "m") false]
   [(string=? s "final") false]
   [(string=? s "e") true]))

;;TESTS:
(begin-for-test
   (check-equal? (next-state "i" "a") "i")
   (check-equal? (next-state "i" "b") "i")
   (check-equal? (next-state "i" "c") "m")
   (check-equal? (next-state "i" "d") "e")
   (check-equal? (next-state "i" "e") "e")
   (check-equal? (next-state "i" "f") "e")
   (check-equal? (next-state "m" "a") "m")
   (check-equal? (next-state "m" "b") "m")
   (check-equal? (next-state "m" "c") "e")
   (check-equal? (next-state "m" "d") "final")
   (check-equal? (next-state "m" "e") "e")
   (check-equal? (next-state "m" "f") "e")
   (check-equal? (next-state "final" "a") "e")
   (check-equal? (next-state "final" "b") "e")
   (check-equal? (next-state "final" "c") "e")
   (check-equal? (next-state "final" "d") "e")
   (check-equal? (next-state "final" "e") "final")
   (check-equal? (next-state "final" "f") "final")
   (check-equal? (next-state "e" "a") "e")
   (check-equal? (next-state "e" "b") "e")
   (check-equal? (next-state "e" "c") "e")
   (check-equal? (next-state "e" "d") "e")
   (check-equal? (next-state "e" "e") "e")
   (check-equal? (next-state "e" "f") "e")
   (check-equal? (initial-state 1) "i")
   (check-equal? (accepting-state? "i") false)
   (check-equal? (accepting-state? "m") false)
   (check-equal? (accepting-state? "final") true)
   (check-equal? (accepting-state? "e") false)
   (check-equal? (error-state? "i") false)
   (check-equal? (error-state? "m") false)
   (check-equal? (error-state? "final") false)
   (check-equal? (error-state? "e") true))

