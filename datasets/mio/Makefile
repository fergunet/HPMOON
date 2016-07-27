#
# Dataset generation
#

# A bash shell will be used for the recipes
SHELL = /bin/bash

# Parameters
DATAGENERATOR = dataset_generator.m
DATA      = dataset.data
DATAINFO  = dataset.info

.PHONY: all
all : $(DATA)


$(DATA): $(DATAGENERATOR)
	@echo "Generating the dataset..."
	@echo
	matlab -nodesktop -nosplash -r $(DATAGENERATOR:.m=)
	@echo

# Cleaning
.PHONY: clean
clean:
	@echo "Cleaning ..."
	@$(RM) *~
	@echo

.PHONY: distclean
distclean: clean
	@$(RM) $(DATA) $(DATAINFO)
