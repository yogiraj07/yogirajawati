;; The first three lines of this file were inserted by DrRacket. They record metadata
;; about the language level of this file in a form that our tools can easily process.
#reader(lib "htdp-intermediate-lambda-reader.ss" "lang")((modname rosters) (read-case-sensitive #t) (teachpacks ()) (htdp-settings #(#t constructor repeating-decimal #f #t none #f () #f)))
(require rackunit)
(require "extras.rkt")
(require 2htdp/universe)
(provide
 make-enrollment
 enrollment-student
 enrollment-class
 make-roster
 roster-classname
 roster-students
 rosterset=?
 roster=?)

;;DATA DEFINATION:

;A Student is a String
;A Class is a String
(define-struct enrollment (student class))
;An Enrollment is a (make-enrollment Student Class)
;INTERPRETATION:
;(make-enrollment s c) represents the assertion that Student s is
;enrolled in Class c.
;TEMPLATE:
;;enrollment-fn : Enrollment->??
#|
(define (enrollment-fn e)
  (...(enrollment-student e)
   ...(enrollment-class e))
|#
;
(define-struct roster (classname students))
;A ClassRoster is a (make-roster Class SetOfStudent)
;INTERPRETATION:
;(make-roster c ss) represents that the Students in Class c are exactly
;the students in set ss.
;TEMPLATE:
;;roster-fn : ClassRoster->??
#|
(define (roster-fn r)
  (...(roster-classname r)
   ...(roster-students r))
|#
;CONSTANTS for TESTING
(define r1 (make-roster "PDP" (list "A" "B" "C")))
(define r2 (make-roster "PDP" (list "B" "A" "C")))
(define r3 (make-roster "IR" (list "A" "B" "C")))
(define r4 (make-roster "MSD" (list "X" "B" "Z")))
(define rosterlist13 (list r1 r3))
(define rosterlist23 (list r2 r3))
(define rosterlist12 (list r2 r3))
(define rosterlist14 (list r1 r4 r3))

;A SetOfStudent is a list of Student without duplication.  

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;roster=? : ClassRoster ClassRoster -> Boolean
;GIVEN: 2 ClassRoster r1 and r2 which needs to be compared
;RETURNS: true iff the two arguments represent the same roster
;EXAMPLE: See Tests
;DESIGN STRATEGY: Use template Roster on r1 and r2
(define (roster=? r1 r2)
  (and (string=? (roster-classname r1) (roster-classname r2))
       (set-equal? (roster-students r1) (roster-students r2) "rosterlist")))
;TESTS:
(begin-for-test
  (check-true (roster=? r1 r2)
              "r1 and r2 are same")
  (check-false (roster=? r1 r3)
               "r1 and r2 are not same"))
;; helper functions
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; my-member? : Student SetOfStudent String -> Boolean
;; GIVEN: A Student x and a set of Student set1 and String str ="rosterlist" or
;;         any other string used to set behavior of member-present? function
;; RETURNS: true iff the x is an element of the set1
;; DESIGN STRATEGY: Use HOF ormap on set1
(define (my-member? x set1 str)
  (ormap
   (;; Student->Boolean
    ;;RETURNS: True iff x is member of SetOfStudent
    lambda (elt)
     (member-present? x elt str))
   set1))

;; member-present? : Student Student String -> Boolean
;; GIVEN: (A Student x and a Student elt, if str="rosterlist") or
;;        (A roster x and roster y,for any other string)
;; RETURNS: true iff the x is equal to elt
;; DESIGN STRATEGY: combine simpler functions
(define (member-present? x elt str)
  (if(string=? str "rosterlist")
       (equal? x elt)
       (roster=? x elt)))
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; subset? : SetOfStudent SetOfStudent String -> Boolean
;; GIVEN : Two SetOfStudent set1 and set2 and String= "rosterlist" or
;;         any other string used to set behavior of my-member? function
;; RETURNS: True if set1 is a subset of set2
;; STRATEGY:Use HOF andmap on set1
(define (subset? set1 set2 str)
  (andmap
   (;;Student->Boolean
    ;;RETURNS: True, iff elt is present in set2
    lambda (elt) (my-member? elt set2 str))
   set1))
  
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;;set-equal? : SetOfStudent SetOfStudent -> Boolean
;;GIVEN : Two SetOfStudent
;;RETURNS : True iff two sets are equal
;;DESIGN STRATEGY: Call simpler functions
(define (set-equal? set1 set2 str)
  (and
   (subset? set1 set2 str)
   (subset? set2 set1 str)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;rosterset=? : SetOfClassRoster SetOfClassRoster -> Boolean
;GIVEN: 2 SetOfClassRoster
;RETURNS: true iff the two arguments represent the same set of rosters
;DESIGN STRATEGY: Combine Simpler Functions
(define (rosterset=? sc1 sc2)
  (set-equal? sc1 sc2 "rosterset"))

 
;;TESTS
;rosterlist13
(begin-for-test
  (check-true (rosterset=? rosterlist12 rosterlist12)
              "rosterlist12 rosterlist12 are same sets")
  (check-true (rosterset=? rosterlist13 rosterlist23)
              "rosterlist12 rosterlist12 are same sets")
  (check-false (rosterset=? rosterlist13 rosterlist14)
               "rosterlist13 rosterlist14 are different sets"))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;enrollments-to-rosters: SetOfEnrollment -> SetOfClassRoster
;GIVEN: a set of enrollments
;RETURNS: the set of class rosters for the given enrollments
;EXAMPLE:
;  (enrollments-to-rosters
;    (list (make-enrollment "John" "PDP")
;          (make-enrollment "Kathryn" "Networks")
;          (make-enrollment "Feng" "PDP")
;          (make-enrollment "Amy" "PDP")
;          (make-enrollment "Amy" "Networks")))
;    =>
; (list
;   (make-roster "PDP" (list "John" "Feng" "Amy"))
;   (make-roster "Networks" (list "Kathryn" "Amy")))
;DESIGN STRATEGY: Use combine simpler functon 
(define (enrollments-to-rosters soe)
  (remove-duplicate(populate-rosters soe))
  )

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;populate-rosters:SetOfEnrollment -> SetOfClassRoster
;;GIVEN: SetOfEnrollment soe
;;RETURNS: SetOfClassRoster which contains duplicate entries of classes
;;DESIGN STRATEGY : Use HOF map on soe
(define (populate-rosters soe)
  (map (;;Enrollment-> Roster
        ;;RETURNS: Converts Enrollment e to a Roster
        lambda (e)
         (make-roster
          (enrollment-class e)
          (combine-duplicate (convert-roster soe) (enrollment-class e)))) soe))
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;convert-roster: SetOfEnrollment -> SetOfClassRoster
;;RETURNS: Each element of SetOfEnrollment is converted to SetOfClassRoster
;;DESIGN STRATEGY : use HOF map on soe

(define (convert-roster soe)
  (map (;;Enrollment -> ClassRoster
        ;;RETURNS: Enrollment converted to Roster 
        lambda (n) (make-roster (enrollment-class n)(enrollment-student n)))
       soe) )

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;combine-duplicate:SetOfClassRoster ClassRoster->SetOfClassRoster
;;RETURNS: A SetOfClassRoster - combining students of same class
;;DESIGN STRATEGY :use HOF foldr on los

(define (combine-duplicate los c)
  (foldr (;;ClassRoster SetOfClassRoster->SetOfClassRoster
          ;;Returns :SetOfClassRoster, if classname of n equals to classname of
          ;;         c ,the function appends roster-students of n to
          ;;         roster-students of c
          lambda(n los)
          (if (string=? (roster-classname n) c)
               (cons (roster-students n) los) 
               los))
          empty
          los))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;remove-duplicate:SetOfClassRoster->SetOfClassRoster
;;RETURNS: SetOfClassRoster with no duplicates for class
;;DESIGN STRATEGY : Use HOF foldr on los
(define (remove-duplicate los)
 (foldr (;;ClassRoster SetOfClassRoster-> SetOfClassRoster
         ;;Given- first element of SetOfClassRoster and Rest of SetOfClassRoster
         ;;RETURNS: SetOfClassRoster without duplicate by adding first element
         ;;         to the set if its not already present
          lambda (n los)
          (if (not (member? n los))
              (cons n los)
              los))
        empty  los))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;CONSTANTS for Testing    
(define input1(list (make-enrollment "John" "PDP")
          (make-enrollment "Kathryn" "Networks")
          (make-enrollment "Feng" "PDP")
          (make-enrollment "Amy" "PDP")
          (make-enrollment "Amy" "Networks")))
(define output1(list
   (make-roster "PDP" (list "John" "Feng" "Amy"))
   (make-roster "Networks" (list "Kathryn" "Amy"))))
(begin-for-test
  (check-equal? (enrollments-to-rosters input1) output1)
  "Converts set of enrollments to set of rosters")



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
