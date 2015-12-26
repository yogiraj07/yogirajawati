;; The first three lines of this file were inserted by DrRacket. They record metadata
;; about the language level of this file in a form that our tools can easily process.
#reader(lib "htdp-beginner-reader.ss" "lang")((modname editor) (read-case-sensitive #t) (teachpacks ()) (htdp-settings #(#t constructor repeating-decimal #f #t none #f () #f)))
(require rackunit)
(require "extras.rkt")
(require 2htdp/universe)
(provide
  make-editor
  editor-pre
  editor-post
  edit
  )


(define-struct editor [pre post])
;;DATA DEFINATION:
;;Editor = (make-editor String String)

;;INTERPRETATION: (make-editor s t) means the text in the editor is
;;(string-append s t) with the cursor displayed between s and t

;;TEMPLATE:
;;editor-fn:ed->??
#|(define(editor-fn ed)
  (...(editor-pre ed)...
   ...(editor-post ed)...))
|#


;;addletter:String String String ->editor
;;GIVEN: The string in the left side of the Cursor as pre and on the right side of the cursor as post and a letter ke that needs to be added
;;RETURNS: editor with letter ke appended to pre

;;Example :
;(edit(make-editor "he" "lo")"i")=(make-editor "hei" "lo"))
;(edit(make-editor "" "lo")"i")=(make-editor "i" "lo"))
;(edit(make-editor "he" "")"i")=(make-editor "hei" ""))

;;DESIGN STRATEGY: Combine Simpler Functions

(define(addletter pre ke post)
  (make-editor (string-append pre ke) post))


;;deleteleft:String String  -> editor
;;GIVEN: The string in the left side of the Cursor as pre and on the right side of the cursor as post 
;;RETURNS:editor with  last character of pre (if any deleted

;;EXAMPLE:
;(edit(make-editor "bos" "ton")"\b")=(make-editor "bo" "ton"))
;(edit(make-editor "" "ton")"\b")=(make-editor "" "ton"))
;(edit(make-editor "" "")"\b")=(make-editor "" ""))
;(edit(make-editor "Bos" "")"\b")= (make-editor "Bo" ""))

;;DESIGN STRATEGY: Combine Simpler Functions

(define (deleteleft pre post)
  (if(>(string-length pre) 0)
    (make-editor (substring pre 0 (- (string-length pre) 1)) post)
    (make-editor "" post)
   ) )


;;moveleft:String String  -> editor
;;GIVEN: The string in the left side of the Cursor as pre and on the right side of the cursor as post 
;;RETURNS: editor with  last character of pre shifted to post

;;EXAMPLE
;(edit(make-editor "Bos" "ton")"left")=(make-editor "Bo" "ston"))
;(edit(make-editor "s" "ton")"left")=(make-editor "" "ston"))
;(edit(make-editor "" "ton")"left")=(make-editor "" "ton"))
;(edit(make-editor "" "")"left")=(make-editor "" ""))
;(edit(make-editor "Boston" "")"left")=(make-editor "Bosto" "n"))

;;DESIGN STRATEGY: Combine Simpler Functions

(define (moveleft pre post)
  (if(> (string-length pre) 0)
     (make-editor (remove-last pre)(string-append (string-last pre) post))
     (make-editor "" post)))

;;moveright:String String  -> editor
;;GIVEN: The string in the left side of the Cursor as pre and on the right side of the cursor as post 
;;RETURNS: editor with  first character of post appended to pre
;;Example
;(edit(make-editor "Bos" "ton")"right") (make-editor "Bost" "on"))
;(edit(make-editor "" "ton")"right") (make-editor "t" "on"))
;(edit(make-editor "Bos" "")"right") (make-editor "Bos" ""))
;(edit(make-editor "" "")"right") (make-editor "" ""))

;;DESIGN STRATEGY: Combine Simpler Functions
  
(define (moveright pre post)
  (if(> (string-length post) 0)
     (make-editor (string-append pre (string-first post)) (remove-first post))
     (make-editor pre "")))

;;string-last: String  -> String
;;GIVEN:String
;;RETURNS: Last character of the string
(define(string-last pre )
   (substring pre (- (string-length pre) 1) (string-length pre)) )

;;remove-last: String  -> String
;;GIVEN: String
;;RETURNS: String with last character deleted
(define (remove-last pre)
       (substring pre 0 (- (string-length pre) 1)))

;;string-first: String  -> Character
;;GIVEN: String
;;RETURNS:First character of String
(define(string-first post)
  (substring post 0 1))

;;remove-first:String  -> String
;;GIVEN: String
;;RETURNS: String with first character deleted
(define (remove-first post)
   (substring post 1 (string-length post)))


;;edit:editor String  -> editor
;;GIVEN: editor and key event in the form of string
;;RETURNS: new  editor according to input "ke"
;;EXAMPLE :
;(edit(make-editor "he" "lo")"i")=(make-editor "hei" "lo"))
;(edit(make-editor "bos" "ton")"\b")=(make-editor "bo" "ton"))
;(edit(make-editor "Bos" "ton")"right") (make-editor "Bost" "on"))

;;DESIGN STRATEGY: Use Cases on "ke"
(define (edit ed ke)
  (cond
    [(key=? ke "\t")(make-editor "" "")]
    [(key=? ke "\r") (make-editor "" "")]
    [(key=? ke "\b") (deleteleft(editor-pre ed) (editor-post ed)) ]
    [(key=? ke "left")(moveleft (editor-pre ed) (editor-post ed))]
    [(key=? ke "right")(moveright (editor-pre ed) (editor-post ed))]
    [else (addletter(editor-pre ed) ke (editor-post ed))]))





(begin-for-test
  (check-equal?(edit(make-editor "he" "lo")"i") (make-editor "hei" "lo"))
  (check-equal?(edit(make-editor "" "lo")"i") (make-editor "i" "lo"))
  (check-equal?(edit(make-editor "he" "")"i") (make-editor "hei" ""))
  (check-equal?(edit(make-editor "" "")"i") (make-editor "i" ""))
  (check-equal?(edit(make-editor "bos" "ton")"\b") (make-editor "bo" "ton"))
  (check-equal?(edit(make-editor "" "ton")"\b") (make-editor "" "ton"))
  (check-equal?(edit(make-editor "" "")"\b")(make-editor "" ""))
  (check-equal?(edit(make-editor "Bos" "")"\b") (make-editor "Bo" ""))
  (check-equal?(edit(make-editor "Bos" "ton")"left") (make-editor "Bo" "ston"))
  (check-equal?(edit(make-editor "s" "ton")"left") (make-editor "" "ston"))
  (check-equal?(edit(make-editor "" "ton")"left") (make-editor "" "ton"))
  (check-equal?(edit(make-editor "" "")"left") (make-editor "" ""))
  (check-equal?(edit(make-editor "Boston" "")"left") (make-editor "Bosto" "n"))
  (check-equal?(edit(make-editor "Bos" "ton")"right") (make-editor "Bost" "on"))
  (check-equal?(edit(make-editor "" "ton")"right") (make-editor "t" "on"))
  (check-equal?(edit(make-editor "Bos" "")"right") (make-editor "Bos" ""))
  (check-equal?(edit(make-editor "" "")"right") (make-editor "" ""))
  (check-equal?(edit(make-editor "He" "llo")"\t") (make-editor "" ""))
  (check-equal?(edit(make-editor "He" "llo")"\r") (make-editor "" ""))
  )

