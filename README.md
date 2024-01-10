A Java library for reading and writing the XML file format of the GnuCash open source accounting software.

# Compatibility
## System Compatibility
Version 1.3 of the library has been tested with 5.4 on Linux (locale de_DE) and OpenJDK 18.0.

## Locale/Language Compatibility
**Caution:** Will only work on systems with the following locale languages:

* English
* French
* Spanish
* German

However, it has **not** been thoroughly tested with all of them, but just on a system with locale de_DE. (for details, cf. the API module documentation).

## Version Compatibility
| Version | Backward Compat. | Note                           |
|---------|------------------|--------------------------------|
| 1.3     | no               | "medium" changes in interfaces |
| 1.2     | no               | minor changes in interfaces    |
| 1.1     | no               | major changes in interfaces    |

# Modules and Further Details

* [API](https://github.com/jross765/jgnucashlib/tree/master/gnucash-api/README.md)

* [Example Programs](https://github.com/jross765/jgnucashlib/tree/master/gnucash-api-examples/README.md)

# Sister Project
This project has a sister project: 

[`JKMyMoneyLib`](https://github.com/jross765/jkmymoneylib)

Both projects do not have the same level of maturity, `JGnuCashLib` is currently much more advanced than `JKMyMoneyLib`. Obviously, the author strives to keep both projects symmetrical and to eventually have them both on a comparable level of maturity.

What is meant by "symmetry" is this context? It means that `JKMyMoneyLib` has literally evolved / is literally evolving from a source-code copy of its sister, this project (i.e., copy the code, then adapt it). Given that KMyMoney and GnuCash are two finance applications with quite a few similarities in their resp. business logics file formats, this approach makes sense. 

Of course, this is a "10.000-metre bird's-eye view". As always in life, things are not that easy as soon as you go into the details. However, looking at the big picture and at least up to the current state of development, the author has managed to keep both projects very similar on a source code level -- so much so that you can partially can use `diff`. You will, however, also see some exceptions here and there where that "low-level-symmetry" is not maintainable. But still, in many of these cases, we can at least maintain a kind of "high-level-symmetry" that prevails on a higher abtraction level.

# Acknowledgements

Special thanks to:

* **Marcus Wolschon (Sofware-Design u. Beratung)** for the original version (from 2005) and the pioneering work (which, by the way, contained way more than what you see here) and for putting the base of this project under the GPL.

    This project is based on Marcus's work. There have been major changes since then, but you can still see where it originated from.

    (Forked from http://sourceforge.net/projects/jgnucashlib / revived in 2017, after some years of enchanted sleep.)

* **Deniss Larka** for kissing the beauty awake and taking care of her for a couple of years.

  (Forked from https://github.com/DenissLarka/jgnucashlib in 2023)