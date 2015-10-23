VERSION 5.00
Begin VB.Form Form1 
   Caption         =   "Form1"
   ClientHeight    =   3030
   ClientLeft      =   120
   ClientTop       =   450
   ClientWidth     =   4560
   LinkTopic       =   "Form1"
   ScaleHeight     =   10950
   ScaleWidth      =   20250
   StartUpPosition =   3  'Windows Default
   Begin VB.CommandButton s 
      Caption         =   "Split"
      Height          =   615
      Left            =   1440
      TabIndex        =   9
      Top             =   5400
      Width           =   1815
   End
   Begin VB.CommandButton Command1 
      Caption         =   "Exit"
      Height          =   615
      Left            =   5280
      TabIndex        =   8
      Top             =   5400
      Width           =   2055
   End
   Begin VB.TextBox Text1 
      Height          =   615
      Index           =   2
      Left            =   4200
      TabIndex        =   5
      Top             =   4440
      Width           =   3615
   End
   Begin VB.TextBox Text1 
      Height          =   615
      Index           =   1
      Left            =   4200
      TabIndex        =   3
      Top             =   3360
      Width           =   3615
   End
   Begin VB.TextBox Text1 
      Height          =   615
      Index           =   0
      Left            =   4200
      TabIndex        =   2
      Top             =   2400
      Width           =   3615
   End
   Begin VB.Label Label2 
      Caption         =   "NOTE for Rohan:for ""Give file input"" try to make command dialog box so that user can select the file....."
      Height          =   975
      Index           =   1
      Left            =   360
      TabIndex        =   7
      Top             =   6240
      Width           =   7335
   End
   Begin VB.Label Label2 
      Caption         =   "FILE SPLITTER"
      Height          =   1095
      Index           =   0
      Left            =   840
      TabIndex        =   6
      Top             =   240
      Width           =   7335
   End
   Begin VB.Label Label1 
      Caption         =   "enter file size to be splitted"
      Height          =   615
      Index           =   2
      Left            =   480
      TabIndex        =   4
      Top             =   4440
      Width           =   2895
   End
   Begin VB.Label Label1 
      Caption         =   "enter file name after assembling"
      Height          =   615
      Index           =   1
      Left            =   600
      TabIndex        =   1
      Top             =   3360
      Width           =   2895
   End
   Begin VB.Label Label1 
      Caption         =   "Give File Input"
      Height          =   615
      Index           =   0
      Left            =   720
      TabIndex        =   0
      Top             =   2400
      Width           =   2895
   End
End
Attribute VB_Name = "Form1"
Attribute VB_GlobalNameSpace = False
Attribute VB_Creatable = False
Attribute VB_PredeclaredId = True
Attribute VB_Exposed = False
Private Sub Label1_Click(Index As Integer)

End Sub
