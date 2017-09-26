#!/usr/bin/env bash
echo "make homepage data to $2"
(cd "$1/../mitmproxy/hooks/testcase/tieba/VipStubForPersonalized"; echo "../VipAutoFRS/$2" > current)
