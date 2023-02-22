# tag-uri obligatorii (nume + comportament identic)
# build    => compileaza toata tema
#             (ATENTIE! E important - NU compilati in tag-urile de run. Sesizati?)
# run-p$ID => ruleaza problema cu ID-ul specificat (1, 2, 3)
# clean    => sterge toate fisierele generate

# TODO

JC = javac
JVM= java 
FILE=

.SUFFIXES: .java .class

.java.class:
	$(JC) $*.java
	

CLASSES = \
		  Walsh.java \
		  Statistics.java \
		  Prinel.java

MAIN1 = Walsh
MAIN2 = Statistics
MAIN3 = Prinel

default: build

build: $(CLASSES:.java=.class)

#classes: $(CLASSES:.java=.class)
	# TODO

run-p1: $(MAIN1).class      # nume necesar
	$(JVM) $(MAIN1) $(FILE)

run-p2: $(MAIN2).class      # nume necesar
	$(JVM) $(MAIN2) $(FILE)

run-p3: $(MAIN3).class      # nume necesar
	$(JVM) $(MAIN3) $(FILE)

clean:		 # nume necesar
	$(RM) *.class
