;; The first three lines of this file were inserted by DrRacket. They record metadata
;; about the language level of this file in a form that our tools can easily process.
#reader(lib "htdp-beginner-reader.ss" "lang")((modname coffee-machine) (read-case-sensitive #t) (teachpacks ()) (htdp-settings #(#t constructor repeating-decimal #f #t none #f () #f)))
(require rackunit)
(require "extras.rkt")
(provide
 initial-machine
 machine-next-state
 machine-output
 machine-remaining-coffee
 machine-remaining-chocolate
 machine-bank
 )

(define-struct MachineState(coffee hot-chocolate bank money-inserted))
;;DATA DEFINATION
;;A MachineState is (make-MachineState NonNeg Int NonNeg Int NonNeg int NonNeg int)
;;INTERPRETATION
;;--coffee represents number of coffee units available in the machine
;;--hot-chocolate represents number of hot-chocolate units available in the machine
;;--bank represents total amount (in cents) deposited by customers in the machine
;;--money-inserted defines the money (in cents) inserted by the customer for the current transaction

;;TEMPLATE
;;MachineState-fn:m->??

#|
(define (MachineState-fn m)
  (...
    MachineState-coffee m
    MachineState-hot-chocolate m
    MachineState-bank m
    MachineState-money-inserted m
     ...))

|#
;;DATA DEFINATION
;;A CustomerInput is one of
;;-- a PosInt          interp: insert the specified amount of money, in cents
;;-- "coffee"          interp: request a coffee
;;-- "hot chocolate"   interp: request a hot chocolate
;;-- "change"          interp: return all the unspent money that the customer has inserted

;;TEMPLATE
;;CustomerInput-fn : c-> ??

#|
(define (CustomerInput-fn c)
  (cond
    [(number? c)...]
    [(string=? c "coffee")...]
    [(string=? c "hot chocolate")....]
    [(string=? c "change")...]
   ))
 
|#

;;DATA DEFINATION
;;A MachineOutput is one of
;;-- "coffee"         interp: machine dispenses a cup of coffee
;;-- "hot chocolate"  interp: machine dispenses a cup of hot chocolate
;;-- "Out of Item"    interp: machine displays "Out of Item"
;;-- a PosInt         interp: machine releases the specified amount of money, in cents
;;-- "Nothing"        interp: the machine does nothing

#|
(define (MachineOutput-fn m)
  (cond
    [(string=? m "coffee")...]
    [(string=? m "hot chocolate")....]
    [(string=? m "Nothing")...]
    [(string=? m "Out of Item")...]
    [(number? m)....]
   ))
 
|#

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


;;initial-machine : NonNegInt NonNegInt -> MachineState
;;GIVEN: a number of cups of coffee and of hot chocolate
;;RETURNS: the state of a machine loaded with the given number of cups of coffee and of hot chocolate, with an empty bank.
;;EXAMPLE:
;;(initial-machine 20 20)=(make-MachineState 20 20 0 0)
;;(initial-machine -20 -20)="")

;;DESIGN STRATEGY: Combine Simpler Functions

(define (initial-machine coffeeNo hot-chocolateNo)
  (if (and (> coffeeNo 0)(> hot-chocolateNo 0))
   (make-MachineState coffeeNo hot-chocolateNo 0 0)
    ""))



;;machine-next-state : MachineState CustomerInput -> MachineState
;;GIVEN: a machine state and a customer input
;;RETURNS: the state of the machine that should follow the customer's input
;;EXAMPLE:
;;(make-MachineState 90 20 0 150) "coffee" )=(make-MachineState 89 20 150 0))
;;(machine-next-state (make-MachineState 0 20 0 10) "coffee" )=(make-MachineState 0 20 0 10)
;;(machine-next-state (make-MachineState 90 20 0 10) "hot chocolate" )=(make-MachineState 90 20 0 10)
;;(machine-next-state (make-MachineState 0 10 10 70) "hot chocolate" )=(make-MachineState 0 9 70 10)
;;(machine-next-state (make-MachineState 0 10 10 70) "change" )=(make-MachineState 0 10 10 0)
;;(machine-next-state (make-MachineState 0 10 10 70) 10 )=(make-MachineState 0 10 10 80)

;;DESIGN STRATEGY: Use Template for CustomerInput on c

(define (machine-next-state m c)
  (cond
    [(number? c) (add-customer-money m c)]
    [(string=? c "coffee")(request-coffee m)]
    [(string=? c "hot chocolate")(request-chocolate m)]
    [(string=? c "change") (request-change m) ]))

;;request-coffee:MachineState->MachineState
;;GIVEN: A MachineState m
;;RETURNS: A new MachineState with reducing available coffee unit by 1, iff coffee units are available and
;;         money inserted by the cutomer is atleast 150 cents 

(define (request-coffee m)
  (if (and (> (MachineState-coffee m) 0) (>= (MachineState-money-inserted m) 150))
     (make-MachineState (- (MachineState-coffee m) 1) (MachineState-hot-chocolate m) (+ (MachineState-bank m) 150)(- (MachineState-money-inserted m) 150))
     (make-MachineState (MachineState-coffee m) (MachineState-hot-chocolate m) (MachineState-bank m) (MachineState-money-inserted m))))

;;request-chocolate:MachineState->MachineState
;;GIVEN: A MachineState m
;;RETURNS: A new MachineState with reducing available hot-chocolate unit by 1, iff hot-chocolate units are available and
;;         money inserted by the cutomer is atleast 60 cents 

(define (request-chocolate m)
  (if(and (> (MachineState-hot-chocolate m) 0) (>= (MachineState-money-inserted m) 60))
     (make-MachineState (MachineState-coffee m) (- (MachineState-hot-chocolate m) 1) (+ (MachineState-bank m) 60)(- (MachineState-money-inserted m) 60))
     (make-MachineState (MachineState-coffee m) (MachineState-hot-chocolate m) (MachineState-bank m) (MachineState-money-inserted m))))

;;request-change:MachineState->MachineState
;;GIVEN: A MachineState m
;;RETURNS: A new MachineState with money-inserted=0, since this money will be dispensed to Customer

(define (request-change m)
  (make-MachineState (MachineState-coffee m) (MachineState-hot-chocolate m) (MachineState-bank m) 0))

;;add-customer-money:MachineState PosInt->MachineState
;;GIVEN: A MachineState m and money-inserted "c" by the customer in cents
;;RETURNS: A new MachineState with money-inserted=c

(define (add-customer-money m c )
  (make-MachineState (MachineState-coffee m) (MachineState-hot-chocolate m) (MachineState-bank m) (+ (MachineState-money-inserted m) c)))


;;machine-output : MachineState CustomerInput -> MachineOutput
;;GIVEN: a machine state and a customer input
;;RETURNS: a MachineOutput that describes the machine's response to the customer input
;;EXAMPLE:
;;(machine-output (make-MachineState 10 10 10 70) "coffee" ) = "coffee"
;;(machine-output (make-MachineState 0 10 10 70) "coffee" ) = "Out of Item"
;;(machine-output (make-MachineState 0 10 10 70) "hot chocolate" ) =  "hot chocolate"
;;(machine-output (make-MachineState 0 0 10 70) "hot chocolate" ) = "Out of Item"
;;(machine-output (make-MachineState 0 10 10 70) "change" ) = 70
;;(machine-output (make-MachineState 0 0 10 70) 44 ) = "Do Nothing"

;;DESIGN STRATEGY: Use Template of CustomerInput on c
(define (machine-output m c)
  ( cond
    [(number? c) "Do Nothing"]
    [(string=? c "coffee") (check-coffee m)]
    [(string=? c "hot chocolate")(check-chocolate m)]
    [(string=? c "change")(MachineState-money-inserted m)]
    [else "Do Nothing"]))

;;check-coffee: MachineState->String
;;GIVEN: A MachineState m
;;RETURNS: If number of coffee available is >0 then String "coffee" is returned else "Out of Item" is returned

(define (check-coffee m)
  (if(> (MachineState-coffee m) 0)
     "coffee"
     "Out of Item"))

;;check-chocolate: MachineState->String
;;GIVEN: A MachineState m
;;RETURNS: If number of hot-chocolate available is >0 then String "hot chocolate" is returned else "Out of Item" is returned

(define (check-chocolate m)
  (if(> (MachineState-hot-chocolate m) 0)
     "hot chocolate"
     "Out of Item"))

;;machine-remaining-coffee : MachineState -> NonNegInt
;;GIVEN: a machine state
;;RETURNS: the number of cups of coffee left in the machine
;;EXAMPLE:
;;(machine-remaining-coffee (make-MachineState 1 20 0 1) )=1
;;(machine-remaining-coffee (make-MachineState 0 20 0 1) )=0
;;DESIGN STRATEGY : Combine Simpler Functions

(define (machine-remaining-coffee m)
  (if(>= (MachineState-coffee m) 0)
  (MachineState-coffee m)
   0))


;;machine-remaining-chocolate : MachineState -> NonNegInt
;;GIVEN: a machine state
;;RETURNS: the number of cups of hot-chocolate left in the machine
;;EXAMPLE:
;;(machine-remaining-chocolate (make-MachineState 10 20 0 1) )=20
;;(machine-remaining-chocolate (make-MachineState 10 0 0 2) )=0
;;DESIGN STRATEGY : Combine Simpler Functions

(define (machine-remaining-chocolate m)
  (if(>= (MachineState-hot-chocolate m) 0)
  (MachineState-hot-chocolate m)
   0))


;;machine-bank : MachineState -> NonNegInt
;;GIVEN: a machine state
;;RETURNS: the amount of money in the machine's bank, in cents
;;EXAMPLE:
;;(machine-bank (make-MachineState 10 0 0 45) )=0
;;(machine-bank (make-MachineState 10 0 -1 3) )=""
;;DESIGN STRATEGY : Combine Simpler Functions

(define (machine-bank m)
  (if(>= (MachineState-bank m) 0 )
     (MachineState-bank m)
      "") )


(begin-for-test
   (check-equal? (initial-machine 20 20)(make-MachineState 20 20 0 0))
   (check-equal? (initial-machine -20 -20)"")
   (check-equal? (machine-remaining-coffee (make-MachineState 1 20 0 0) )1)
   (check-equal? (machine-remaining-coffee (make-MachineState -90 20 0 0) )0)
   (check-equal? (machine-remaining-chocolate (make-MachineState 10 20 0 0) )20)
   (check-equal? (machine-remaining-chocolate (make-MachineState 10 -10 0 0) )0)
   (check-equal? (machine-bank (make-MachineState 10 0 0 0) )0)
   (check-equal? (machine-bank (make-MachineState 10 0 -1 0) )"")
   (check-equal? (machine-next-state (make-MachineState 90 20 0 150) "coffee" )(make-MachineState 89 20 150 0))
   (check-equal? (machine-next-state (make-MachineState 0 20 0 10) "coffee" )(make-MachineState 0 20 0 10))
   (check-equal? (machine-next-state (make-MachineState 90 20 0 10) "hot chocolate" )(make-MachineState 90 20 0 10))
   (check-equal? (machine-next-state (make-MachineState 0 10 10 70) "hot chocolate" )(make-MachineState 0 9 70 10))
   (check-equal? (machine-next-state (make-MachineState 0 10 10 "") "change" )(make-MachineState 0 10 10 0))
   (check-equal? (machine-next-state (make-MachineState 90 20 0 150) 100 )(make-MachineState 90 20 0 250))
   (check-equal? (machine-output (make-MachineState 10 10 10 70) "coffee" ) "coffee")
   (check-equal? (machine-output (make-MachineState 0 10 10 70) "coffee" ) "Out of Item")
   (check-equal? (machine-output (make-MachineState 0 10 10 70) "hot chocolate" ) "hot chocolate")
   (check-equal? (machine-output (make-MachineState 0 0 10 70) "hot chocolate" ) "Out of Item")
   (check-equal? (machine-output (make-MachineState 0 10 10 70) "change" ) 70)
   (check-equal? (machine-output (make-MachineState 0 0 10 70) 44 ) "Do Nothing")
   (check-equal? (machine-output (make-MachineState 0 0 10 70) "abc" ) "Do Nothing")

   (check-equal? (initial-machine 20 20)(make-MachineState 20 20 0 0))
   (check-equal? (machine-output (make-MachineState 20 20 0 0) 160 ) "Do Nothing")  ;;160 cents inserted
   (check-equal? (machine-next-state (make-MachineState 20 20 0 0) 160 )(make-MachineState 20 20 0 160))
   (check-equal? (machine-output (make-MachineState 20 20 0 160) "coffee" ) "coffee")
   (check-equal? (machine-next-state (make-MachineState 20 20 0 160) "coffee" )(make-MachineState 20 19 150 10))
   (check-equal? (machine-output (make-MachineState 20 19 150 10) "change" ) 10)
   (check-equal? (machine-next-state (make-MachineState 20 19 150 10) "change" )(make-MachineState 20 19 150 0))
   
   
   )