# citedownutils 

Utilities for working with source content in `citedown` (that is, markdown extended to support citation in URN notation).

Currently in planning stages only.

Depends on the cd2md library.  Supersedes the former mdweb library.

Planned functionality:

- generate a web site from markdown source, maintaining subdirectory organization of content, and optionally configurable at any level of the directory hierarchy
- using cd2md to generate pure markdown with URNs resolved to URLs, flatten citedown source in subdirectories into markdown in a single directory, including
    - a `manifest.json` file for use with beautiful docs
    - a `Books.txt` file for use with leanpub
    - quoted images retrieved and stored locally, and linked with markdown ! notation for embedded images

