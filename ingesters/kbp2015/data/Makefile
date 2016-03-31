
# For development only.
# You may need to replace the ssh target (e.g. test2b) with something that works on your machine.
LDC2015E77:
	rsync -avz test2b:/export/common/data/corpora/LDC/LDC2015E77 .

# This contains the slot filling data
LDC2015E100:
	rsync -avz test2b:/export/common/data/corpora/LDC/LDC2015E100 .

# This contains the xdoc entity coref
LDC2015E81:
	rsync -avz test1b:/export/common/data/corpora/LDC/LDC2015E81 .


# Ingested data Tongfei
pre-standoff-communications:
	mkdir pre-standoff-communications
	scp test1b:/expts/projects/tchen/data/tac2015/LDC2015E77/*.tar.gz pre-standoff-communications

