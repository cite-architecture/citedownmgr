---
layout: page
title: citedownmgr
---

## Background

There are lots of good tools for working with markdown. The `cd2md` library makes it easy to convert a citedown document relying on a single repository to generic markdown. But many tools for working with collections of markdown source files either require or make it much easier for you if your source material is in a single, flat directory.

`citedownmgr` lets you organize citedown source in a normal directory hierarchy, and supports:

- flattening your citedown file tree into a single directory of generic markdown with metadata for tools like `leanpub` and `bfdocs`
- generating a web site that mirrors the directory hierarchy of your source

## Currently planned functionality

For first release candidate:

- using cd2md to generate pure markdown with URNs resolved to URLs, flatten citedown source in subdirectories into markdown in a single directory, including
    - a `Books.txt` file for use with leanpub
    - quoted images retrieved and stored locally, and linked with markdown ! notation for embedded images