;; The first three lines of this file were inserted by DrRacket. They record metadata
;; about the language level of this file in a form that our tools can easily process.
#reader(lib "htdp-intermediate-lambda-reader.ss" "lang")((modname class-lists) (read-case-sensitive #t) (teachpacks ()) (htdp-settings #(#t constructor repeating-decimal #f #t none #f () #f)))
;;class-lists -> HOF Implemetation
(require rackunit)
(require "extras.rkt")
(require 2htdp/universe)
(provide
 felleisen-roster
 shivers-roster
 make-slip
 slip-color
 slip-name1
 slip-name2
 )

;;DATA DEFINATION :
;;A Color is one of
;;-- "yellow"
;;-- "blue"

;;INTERPRETATION : Self-evident

;;Template
;;color-fn-> Color->??
#|
(define (color-fn c)
  (cond
    [(string=? c "yellow")...])
    [(string=? c "blue")....]))
|#




;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(define-struct slip (color name1 name2))
;;A Slip is a (make-slip Color String String)
;;INTERPRETATION:
;;color is the Color of the slip which can be either yellow or blue
;;name1 is the first or last name of the student
;;name2 is the first/last name of the student
;;NOTE: if name1=first name then name2=last name and vice versa

;;TEMPLATE

;;slip-fn-> Slip->??

#|
(define (slip-fn s)
  (...(slip-color s)
   ...(slip-name1 s)
   ...(slip-name2 s)))
|#
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;ListOfSlip
;; A ListOfSlip is either
;; --empty
;; --(cons Rectangle LOS)

;; los-fn : ListOfSlip -> ??
;; (define (los-fn los)
;;   (cond
;;     [(empty? los) ...]
;;     [else (...
;;             (slip-fn (first los))
;;             (los-fn (rest los)))]))
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;;CONSTANTS for Testing
(define input (list(make-slip  "yellow" "Yogi" "Awati")
                 (make-slip  "blue" "Yogi" "Awati")
                 (make-slip  "yellow" "Awati" "Yogi")
                 (make-slip "yellow" "Ash" "Kal")
                 (make-slip  "blue" "Ank" "Shel")
                 (make-slip  "yellow" "Ank" "Shel")
                 (make-slip  "yellow" "Shel" "Ank" )
                 (make-slip  "yellow" "Ank" "Shel")
                 (make-slip  "yellow" "Yogi" "Raj")
                 (make-slip  "blue" "Awati" "Yogi")
                 (make-slip  "blue" "Yogi" "Raj")))

(define yellow-list (list(make-slip "yellow" "Yogi" "Awati" )
                         (make-slip "yellow" "Awati" "Yogi" )
                         (make-slip "yellow" "Ash" "Kal" )))

(define unique-yellow (list(make-slip "yellow" "Awati" "Yogi")
                           (make-slip "yellow" "Ash" "Kal" )
                           (make-slip "yellow" "Ank" "Shel")
                           (make-slip "yellow" "Yogi" "Raj")))

(define unique-blue (list(make-slip  "blue" "Ank" "Shel")
                         (make-slip  "blue"  "Awati" "Yogi")
                         (make-slip  "blue" "Yogi" "Raj")))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;felleisen-roster : ListOfSlip -> ListOfSlip
;;GIVEN: a list of slips
;;RETURNS: a list of slips containing all the students in Professor
;;Felleisen's class, without duplication.
;;EXAMPLE: See Tests below
;;DESIGN STRATEGY: Combine Simpler Functions
(define (felleisen-roster los)
        (remove-duplicate (populate-yellow-list los)))
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;populate-yellow-list : LOS -> LOS
;;GIVEN: ListOfSlips which contains duplicates of yellow and blue slips 
;;RETURNS: LOS with yellow slips only
;;DESIGN STRATEGY : Use HOF filter on los

(define (populate-yellow-list  los )
  (filter (;;Slip-> Boolean
           ;;RETURNS: Returns True iff slip is yellow color 
           lambda (n)(check-slip-yellow? n)) los))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;;remove-duplicate: LOS->LOS
;;GIVEN: LOS of either yellow or blue color
;;RETURNS: Unique same color LOS with no duplicates 
;;DESIGN STRATEGY : Use HOF foldr on los
(define (remove-duplicate los)
 (foldr (;;String LOS-> LOS
         ;;Given- first element of LOS and Rest of LOS
         ;;RETURNS: LOS without duplicate
          lambda (n los)
          (if (check-matches? n los)
              los
              (cons n los)
              ))
        empty  los))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;;check-matches? : Slip LOS-> Boolean
;;GIVEN: A Slip s and LOS 
;;RETURNS: True iff name1 and name2 matches with any of the slip in LOS
;;DESIGN STRATEGY : Use HOF ormap on los

(define (check-matches? s los)
  (ormap (;;Slip->Boolean
          ;;RETURNS: True, iff name of Slip n matches with any other slip of LOS
          lambda(n)(name-matches? n s)) los))
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;;name-matches? : S LOS->Boolean
;;GIVEN: A Slip s and LOS 
;;RETURNS: True iff name1 and name2 matches with any of the slip in LOS
;;DESIGN STRATEGY : Combine Simple Functions
(define (name-matches? s1 s2)
   (or ( and (string=? (slip-name1 s1) (slip-name1 s2))
                   (string=? (slip-name2 s1) (slip-name2 s2)))
      (and ( string=? (slip-name1 s1) (slip-name2 s2))
                   (string=? (slip-name2 s1) (slip-name1 s2)))))
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;;check-slip-yellow?: Slip->Boolean
;;GIVEN: A Slip s which can be either of yellow or blue color
;;RETURNS: True iff the slip is of yellow color
;;DESIGN STRATEGY : Use template for Slip on s

(define (check-slip-yellow? s)(string=? (slip-color s) "yellow"))

;;TESTS:
(begin-for-test
  (check-equal? (felleisen-roster input) unique-yellow)
   "Outputs unique yellow list")

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;;shivers-roster: ListOfSlip -> ListOfSlip
;;GIVEN: a list of slips
;;RETURNS:a list of slips containing all the students in Professor
;;        Shivers' class, without duplication.
;;EXAMPLE: see tests
;;DESIGN STRATEGY: Combine Simpler Functions

(define (shivers-roster los)
  (remove-duplicate (populate-blue-list los)))
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;;populate-blue-list : LOS -> LOS
;;GIVEN: ListOfSlips which contains duplicates mixed yellow and blue slips 
;;RETURNS: LOS with blue slips only
;;DESIGN STRATEGY : Use HOF filter on los
(define (populate-blue-list  los )
  (filter (;;Slip->Boolean
           ;;RETURNS: True, iff Slip n is of blue color
           lambda (n)(check-slip-blue? n)) los))
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;;check-slip-blue?: Slip->Boolean
;;GIVEN: A Slip s which can be either of yellow or blue color
;;RETURNS: True iff the slip is of blue color
;;DESIGN STRATEGY : Use template for Slip on s

(define (check-slip-blue? s)(string=? (slip-color s) "blue"))


;;TESTS:
(begin-for-test
  (check-equal? (shivers-roster input) unique-blue)
  "Outputs unique blue list")

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

