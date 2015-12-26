;; The first three lines of this file were inserted by DrRacket. They record metadata
;; about the language level of this file in a form that our tools can easily process.
#reader(lib "htdp-beginner-reader.ss" "lang")((modname q5) (read-case-sensitive #t) (teachpacks ()) (htdp-settings #(#t constructor repeating-decimal #f #t none #f () #f)))
;;;Problem 22 - To delete ith position from string str
(require rackunit)
(require "extras.rkt")
(provide string-delete)
;no data defination required

;;string-delete: String Integer->String
;;GIVEN : String str and the number "i". 
;;RETURNS : String with ith position deleted is returned
;;EXAMPLES:
;;(string-delete "University" 0)="niversity"
;;(string-delete "Boston" 3)="Boson
;;DESIGN STRATEGY : Combine Simpler Functions

(define (string-delete str i)(if (<= (string-length str) i)
                                  "Invalid Parameters. So deletion cannot take place"
                                 (string-append (substring str 0 i)
                                               (substring str (+ i 1) (string-length str) ) )))
                                
                              
                                            
                                             
   
;; TESTS
(begin-for-test
  (check-equal? (string-delete "University" 0) "niversity"
    "For input (University 0)=niversity")
  (check-equal? (string-delete "Boston" 6) "Invalid Parameters. So deletion cannot take place"))
