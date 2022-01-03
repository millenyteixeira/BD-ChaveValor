JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) -Xlint:unchecked $*.java
CLASSES = \
	base/simplebd/CRUD.java \
    base/simplebd/Lzw.java \
    base/simplebd/BinaryOut.java \
    base/simplebd/BinaryIn.java \
    base/simplebd/Huffman.java \
    base/simplebd/selectionSort.java \
    base/simplebd/HashExtensivel.java \
    base/simplebd/Main.java
default: classes
classes: $(CLASSES:.java=.class)