;; The first three lines of this file were inserted by DrRacket. They record metadata
;; about the language level of this file in a form that our tools can easily process.
#reader(lib "htdp-beginner-reader.ss" "lang")((modname q4) (read-case-sensitive #t) (teachpacks ()) (htdp-settings #(#t constructor repeating-decimal #f #t none #f () #f)))
;;;Problem 21 - To insert "_" at ith position of String str
(require rackunit)
(require "extras.rkt")
(provide string-insert)
;no data defination required

;;string-insert: String Integer->String
;;GIVEN : String str and the number "i". "i" varies from 0 to lenght of the string
;;RETURNS : String with "_" inserted at ith position
;;EXAMPLES:
;;(string-insert "Hello" 2)="He_llo"
;;(string-insert "Boston" 3)="Bos_ton"
;;DESIGN STRATEGY : Combine Simpler Functions

(define (string-insert str i)(if (< (string-length str) i)
                                 "Invalid Parameters. So insertion cannot take place"
                                 (string-append (substring str 0 i)
                                 "_"
                                 (substring str i (string-length str) ) )))
                                
                              
                                            
                                             
   
;; TESTS
(begin-for-test
  (check-equal? (string-insert "Hello" 2) "He_llo" 
    "For input (Hello 2)=He_llo")
  (check-equal? (string-insert "Boston" 3) "Bos_ton"
    "For input (Boston 3)=Bost_on"))
