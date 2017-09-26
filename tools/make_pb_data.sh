#!/usr/bin/env bash
echo "make pb data to $2"
(cd "$1/../mitmproxy/hooks/testcase/tieba/VipStubForPB"; echo "../VipAutoPB/$2" > current)
