MensaPlan
=========

Build
-----

```bash
$ gradlew assembleDebug
```

Card-Reading-Details
--------------------

Sector 6 seems to contain the information
 about `Current Money` and `last Transaction`

lastTransaction = currentMoney - v2

currently read          | Should be
------------------------|----------
398 last transaction    | 330 last transaction
182 balance             | 142 balance

```
Sector 6 (0x06)
[18] rW-  4C 7E 2B 03 00 93 00 01 00 74 2B 6F 1A FD EA 7B // program does not look at this
                                         v1'
                                        |---|
ArrayIdx: 0  1  2  3  4  5  6  7  8  9  10 11 12 13 14 15
[19] rW-  65 64 05 66 61 50 63 62 73 6C E0 6F 69 2A 87 B3 // "data" starts at beginning of this line

           v2'                   datakey valuekey'
          |---|                     || |---|
ArrayIdx: 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31
[1A] rW-  55 54 14 F0 50 51 36 53 5C 6C 6F 6E 69 B3 43 81 // "data" ends at end of this line

valuekey' = 0x6F6E
datakey = 0x6C

[1B] WXW  A0:A1:A2:A3:A4:A5 78:77:88 00 XX:XX:XX:XX:XX:XX

```