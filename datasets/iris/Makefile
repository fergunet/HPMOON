#
# Pre-processing of iris dataset
#

# A bash shell will be used for the recipes
SHELL = /bin/bash

# Parameters
INDATA      = iris.data
OUTDATA     = iris2.data


.PHONY: all
all : $(OUTDATA)


$(OUTDATA): $(INDATA)
	@echo "Preprocessing..."
	@awk '{gsub(/,/,"\t");print}' $< >> $(OUTDATA)
	@echo

# Cleaning
.PHONY: clean
clean:
	@echo "Cleaning ..."
	@$(RM) $(OUTDATA) *~
	@echo

