;; The first three lines of this file were inserted by DrRacket. They record metadata
;; about the language level of this file in a form that our tools can easily process.
#reader(lib "htdp-beginner-reader.ss" "lang")((modname q2) (read-case-sensitive #t) (teachpacks ()) (htdp-settings #(#t constructor repeating-decimal #f #t none #f () #f)))
;;;Problem 15 - To extract first 1String from a non-empty string
(require rackunit)
(require "extras.rkt")
(provide string-first)
;;no data defination required

;;string-first: String->String

;;GIVEN : String str
;;RETURNS : The first 1String of the input string str
;;EXAMPLES:
;;(string-first "North")="N"
;;(string-first "Boston")="B"
;;DESIGN STRATEGY : Combine Simpler Functions

(define (string-first str)
  (substring str 0 1))

;; TESTS
(begin-for-test
  (check-equal? (string-first "North") "N" 
    "The first 1String of String North is N")
  (check-equal? (string-first "Boston") "B"
    "The first 1String of String Boston is B"))
