# Code Coverage
The code coverage describes how much of the written code is tested by the
unit tests. We generate coverage data by using the cobertura application and
running the tests via JUnit.

## Initializing cobertura
To initialize cobertura we use the following command in the project home directory

    cobertura-instrument -basedir <path to implementation> -basedir <path to tests>

## Running the unit tests
The unit tests are run with the following command:

    java -cp .:$(find /usr/share/ -name "*.jar" | xargs | sed "s/\ /:/g"):$(find . -name "*.jar" | xargs | sed "s/\ /:/g"):$(find . -name "*.class" | grep -o -P ".*\/(xn--clchc0ea0b2g2a9gcd|xn--hlcj6aya9esc7a|xn--hgbk6aj7f53bba|xn--xkc2dl3a5ee0h|xn--mgberp4a5d4ar|xn--11b5bs3a9aj6g|xn--xkc2al3hye2a|xn--80akhbyknj4f|xn--mgbc0a9azcg|xn--lgbbat1ad8j|xn--mgbx4cd0ab|xn--mgbbh1a71e|xn--mgbayh7gpa|xn--mgbaam7a8h|xn--9t4b11yi5a|xn--ygbi2ammx|xn--yfro4i67o|xn--fzc2c9e2c|xn--fpcrj9c3d|xn--ogbpf8fl|xn--mgb9awbf|xn--kgbechtv|xn--jxalpdlp|xn--3e0b707e|xn--s9brj9c|xn--pgbs0dh|xn--kpry57d|xn--kprw13d|xn--j6w193g|xn--h2brj9c|xn--gecrj9c|xn--g6w251d|xn--deba0ad|xn--80ao21a|xn--45brj9c|xn--0zwm56d|xn--zckzah|xn--wgbl6a|xn--wgbh1c|xn--o3cw4h|xn--fiqz9s|xn--fiqs8s|xn--90a3ac|xn--p1ai|travel|museum|post|name|mobi|jobs|info|coop|asia|arpa|aero|xxx|tel|pro|org|net|mil|int|gov|edu|com|cat|biz|zw|zm|za|yt|ye|ws|wf|vu|vn|vi|vg|ve|vc|va|uz|uy|us|uk|ug|ua|tz|tw|tv|tt|tr|tp|to|tn|tm|tl|tk|tj|th|tg|tf|td|tc|sz|sy|sx|sv|su|st|sr|so|sn|sm|sl|sk|sj|si|sh|sg|se|sd|sc|sb|sa|rw|ru|rs|ro|re|qa|py|pw|pt|ps|pr|pn|pm|pl|pk|ph|pg|pf|pe|pa|om|nz|nu|nr|np|no|nl|ni|ng|nf|ne|nc|na|mz|my|mx|mw|mv|mu|mt|ms|mr|mq|mp|mo|mn|mm|ml|mk|mh|mg|me|md|mc|ma|ly|lv|lu|lt|ls|lr|lk|li|lc|lb|la|kz|ky|kw|kr|kp|kn|km|ki|kh|kg|ke|jp|jo|jm|je|it|is|ir|iq|io|in|im|il|ie|id|hu|ht|hr|hn|hm|hk|gy|gw|gu|gt|gs|gr|gq|gp|gn|gm|gl|gi|gh|gg|gf|ge|gd|gb|ga|fr|fo|fm|fk|fj|fi|eu|et|es|er|eg|ee|ec|dz|do|dm|dk|dj|de|cz|cy|cx|cw|cv|cu|cr|co|cn|cm|cl|ck|ci|ch|cg|cf|cd|cc|ca|bz|by|bw|bv|bt|bs|br|bo|bn|bm|bj|bi|bh|bg|bf|be|bd|bb|ba|az|ax|aw|au|at|as|ar|aq|ao|an|am|al|ai|ag|af|ae|ad|ac)\/" | sed "s/[^/]*\/$//" | sort | uniq | xargs | sed "s/\ /:/g") org.junit.runner.JUnitCore $(find . -name "*.class" | grep "test" | find . -name "*.class" | grep -o -P "\/(xn--clchc0ea0b2g2a9gcd|xn--hlcj6aya9esc7a|xn--hgbk6aj7f53bba|xn--xkc2dl3a5ee0h|xn--mgberp4a5d4ar|xn--11b5bs3a9aj6g|xn--xkc2al3hye2a|xn--80akhbyknj4f|xn--mgbc0a9azcg|xn--lgbbat1ad8j|xn--mgbx4cd0ab|xn--mgbbh1a71e|xn--mgbayh7gpa|xn--mgbaam7a8h|xn--9t4b11yi5a|xn--ygbi2ammx|xn--yfro4i67o|xn--fzc2c9e2c|xn--fpcrj9c3d|xn--ogbpf8fl|xn--mgb9awbf|xn--kgbechtv|xn--jxalpdlp|xn--3e0b707e|xn--s9brj9c|xn--pgbs0dh|xn--kpry57d|xn--kprw13d|xn--j6w193g|xn--h2brj9c|xn--gecrj9c|xn--g6w251d|xn--deba0ad|xn--80ao21a|xn--45brj9c|xn--0zwm56d|xn--zckzah|xn--wgbl6a|xn--wgbh1c|xn--o3cw4h|xn--fiqz9s|xn--fiqs8s|xn--90a3ac|xn--p1ai|travel|museum|post|name|mobi|jobs|info|coop|asia|arpa|aero|xxx|tel|pro|org|net|mil|int|gov|edu|com|cat|biz|zw|zm|za|yt|ye|ws|wf|vu|vn|vi|vg|ve|vc|va|uz|uy|us|uk|ug|ua|tz|tw|tv|tt|tr|tp|to|tn|tm|tl|tk|tj|th|tg|tf|td|tc|sz|sy|sx|sv|su|st|sr|so|sn|sm|sl|sk|sj|si|sh|sg|se|sd|sc|sb|sa|rw|ru|rs|ro|re|qa|py|pw|pt|ps|pr|pn|pm|pl|pk|ph|pg|pf|pe|pa|om|nz|nu|nr|np|no|nl|ni|ng|nf|ne|nc|na|mz|my|mx|mw|mv|mu|mt|ms|mr|mq|mp|mo|mn|mm|ml|mk|mh|mg|me|md|mc|ma|ly|lv|lu|lt|ls|lr|lk|li|lc|lb|la|kz|ky|kw|kr|kp|kn|km|ki|kh|kg|ke|jp|jo|jm|je|it|is|ir|iq|io|in|im|il|ie|id|hu|ht|hr|hn|hm|hk|gy|gw|gu|gt|gs|gr|gq|gp|gn|gm|gl|gi|gh|gg|gf|ge|gd|gb|ga|fr|fo|fm|fk|fj|fi|eu|et|es|er|eg|ee|ec|dz|do|dm|dk|dj|de|cz|cy|cx|cw|cv|cu|cr|co|cn|cm|cl|ck|ci|ch|cg|cf|cd|cc|ca|bz|by|bw|bv|bt|bs|br|bo|bn|bm|bj|bi|bh|bg|bf|be|bd|bb|ba|az|ax|aw|au|at|as|ar|aq|ao|an|am|al|ai|ag|af|ae|ad|ac)\/.*" | sed "s/^\///" | sed "s/\.class$//g" | xargs | sed "s/\//\./g")

This works as follows:

    java -cp .:

this is the option to define the `CLASSPATH` variable as the `CLASSPATH` + the following

    $(find /usr/share/ -name "*.jar" | xargs | sed "s/\ /:/g"):$(find . -name "*.jar" | xargs | sed "s/\ /:/g")

every jar in `/usr/share` in `CLASSPATH` notation and

    $(find . -name "*.class" | grep -o -P ".*\/(xn--clchc0ea0b2g2a9gcd|xn--hlcj6aya9esc7a|xn--hgbk6aj7f53bba|xn--xkc2dl3a5ee0h|xn--mgberp4a5d4ar|xn--11b5bs3a9aj6g|xn--xkc2al3hye2a|xn--80akhbyknj4f|xn--mgbc0a9azcg|xn--lgbbat1ad8j|xn--mgbx4cd0ab|xn--mgbbh1a71e|xn--mgbayh7gpa|xn--mgbaam7a8h|xn--9t4b11yi5a|xn--ygbi2ammx|xn--yfro4i67o|xn--fzc2c9e2c|xn--fpcrj9c3d|xn--ogbpf8fl|xn--mgb9awbf|xn--kgbechtv|xn--jxalpdlp|xn--3e0b707e|xn--s9brj9c|xn--pgbs0dh|xn--kpry57d|xn--kprw13d|xn--j6w193g|xn--h2brj9c|xn--gecrj9c|xn--g6w251d|xn--deba0ad|xn--80ao21a|xn--45brj9c|xn--0zwm56d|xn--zckzah|xn--wgbl6a|xn--wgbh1c|xn--o3cw4h|xn--fiqz9s|xn--fiqs8s|xn--90a3ac|xn--p1ai|travel|museum|post|name|mobi|jobs|info|coop|asia|arpa|aero|xxx|tel|pro|org|net|mil|int|gov|edu|com|cat|biz|zw|zm|za|yt|ye|ws|wf|vu|vn|vi|vg|ve|vc|va|uz|uy|us|uk|ug|ua|tz|tw|tv|tt|tr|tp|to|tn|tm|tl|tk|tj|th|tg|tf|td|tc|sz|sy|sx|sv|su|st|sr|so|sn|sm|sl|sk|sj|si|sh|sg|se|sd|sc|sb|sa|rw|ru|rs|ro|re|qa|py|pw|pt|ps|pr|pn|pm|pl|pk|ph|pg|pf|pe|pa|om|nz|nu|nr|np|no|nl|ni|ng|nf|ne|nc|na|mz|my|mx|mw|mv|mu|mt|ms|mr|mq|mp|mo|mn|mm|ml|mk|mh|mg|me|md|mc|ma|ly|lv|lu|lt|ls|lr|lk|li|lc|lb|la|kz|ky|kw|kr|kp|kn|km|ki|kh|kg|ke|jp|jo|jm|je|it|is|ir|iq|io|in|im|il|ie|id|hu|ht|hr|hn|hm|hk|gy|gw|gu|gt|gs|gr|gq|gp|gn|gm|gl|gi|gh|gg|gf|ge|gd|gb|ga|fr|fo|fm|fk|fj|fi|eu|et|es|er|eg|ee|ec|dz|do|dm|dk|dj|de|cz|cy|cx|cw|cv|cu|cr|co|cn|cm|cl|ck|ci|ch|cg|cf|cd|cc|ca|bz|by|bw|bv|bt|bs|br|bo|bn|bm|bj|bi|bh|bg|bf|be|bd|bb|ba|az|ax|aw|au|at|as|ar|aq|ao|an|am|al|ai|ag|af|ae|ad|ac)\/" | sed "s/[^/]*\/$//" | sort | uniq | xargs | sed "s/\ /:/g")

everything in this directory until a top-level domain name that is unique

    org.junit.runner.JUnitCore org.junit.runner.JUnitCore

is the class that runs JUnitCore

    $(find . -name "*.class" | grep "test" | find . -name "*.class" | grep -o -P "\/(xn--clchc0ea0b2g2a9gcd|xn--hlcj6aya9esc7a|xn--hgbk6aj7f53bba|xn--xkc2dl3a5ee0h|xn--mgberp4a5d4ar|xn--11b5bs3a9aj6g|xn--xkc2al3hye2a|xn--80akhbyknj4f|xn--mgbc0a9azcg|xn--lgbbat1ad8j|xn--mgbx4cd0ab|xn--mgbbh1a71e|xn--mgbayh7gpa|xn--mgbaam7a8h|xn--9t4b11yi5a|xn--ygbi2ammx|xn--yfro4i67o|xn--fzc2c9e2c|xn--fpcrj9c3d|xn--ogbpf8fl|xn--mgb9awbf|xn--kgbechtv|xn--jxalpdlp|xn--3e0b707e|xn--s9brj9c|xn--pgbs0dh|xn--kpry57d|xn--kprw13d|xn--j6w193g|xn--h2brj9c|xn--gecrj9c|xn--g6w251d|xn--deba0ad|xn--80ao21a|xn--45brj9c|xn--0zwm56d|xn--zckzah|xn--wgbl6a|xn--wgbh1c|xn--o3cw4h|xn--fiqz9s|xn--fiqs8s|xn--90a3ac|xn--p1ai|travel|museum|post|name|mobi|jobs|info|coop|asia|arpa|aero|xxx|tel|pro|org|net|mil|int|gov|edu|com|cat|biz|zw|zm|za|yt|ye|ws|wf|vu|vn|vi|vg|ve|vc|va|uz|uy|us|uk|ug|ua|tz|tw|tv|tt|tr|tp|to|tn|tm|tl|tk|tj|th|tg|tf|td|tc|sz|sy|sx|sv|su|st|sr|so|sn|sm|sl|sk|sj|si|sh|sg|se|sd|sc|sb|sa|rw|ru|rs|ro|re|qa|py|pw|pt|ps|pr|pn|pm|pl|pk|ph|pg|pf|pe|pa|om|nz|nu|nr|np|no|nl|ni|ng|nf|ne|nc|na|mz|my|mx|mw|mv|mu|mt|ms|mr|mq|mp|mo|mn|mm|ml|mk|mh|mg|me|md|mc|ma|ly|lv|lu|lt|ls|lr|lk|li|lc|lb|la|kz|ky|kw|kr|kp|kn|km|ki|kh|kg|ke|jp|jo|jm|je|it|is|ir|iq|io|in|im|il|ie|id|hu|ht|hr|hn|hm|hk|gy|gw|gu|gt|gs|gr|gq|gp|gn|gm|gl|gi|gh|gg|gf|ge|gd|gb|ga|fr|fo|fm|fk|fj|fi|eu|et|es|er|eg|ee|ec|dz|do|dm|dk|dj|de|cz|cy|cx|cw|cv|cu|cr|co|cn|cm|cl|ck|ci|ch|cg|cf|cd|cc|ca|bz|by|bw|bv|bt|bs|br|bo|bn|bm|bj|bi|bh|bg|bf|be|bd|bb|ba|az|ax|aw|au|at|as|ar|aq|ao|an|am|al|ai|ag|af|ae|ad|ac)\/.*" | sed "s/^\///" | sed "s/\.class$//g" | xargs | sed "s/\//\./g")

are all the classes (that have "test" in their name) including their domianname 

### Possible Bugs
There might be a case where a project domain is not compatible to the standard
java naming conventions and has therefore not the top level domain as the main
folder. 
If this is the case the unit tests have to be run manually.

## generating coverage data
To generate the reports use the following command

	cobertura-report --destination <path to destdir>

The results will be written into html files which can be viewed in a browser.
