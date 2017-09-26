#!/usr/bin/env bash
echo "make frs data to $2"
(cd "$1/../mitmproxy/hooks/testcase/tieba/VipStubForFRS"; echo "../VipAutoFRS/$2" > current)
