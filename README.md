# velocity-sync

[![standard-readme compliant](https://img.shields.io/badge/standard--readme-OK-green.svg?style=flat-square)](https://github.com/RichardLitt/standard-readme)
[![GitHub](https://img.shields.io/github/license/self-crafted/velocity-sync?style=flat-square&color=b2204c)](https://github.com/self-crafted/velocity-sync/blob/master/LICENSE)
[![GitHub Repo stars](https://img.shields.io/github/stars/self-crafted/velocity-sync?style=flat-square)](https://github.com/self-crafted/velocity-sync/stargazers)
[![GitHub forks](https://img.shields.io/github/forks/self-crafted/velocity-sync?style=flat-square)](https://github.com/self-crafted/velocity-sync/network/members)
[![GitHub release (latest SemVer)](https://img.shields.io/github/v/release/self-crafted/velocity-sync?style=flat-square)](https://github.com/self-crafted/velocity-sync/releases/latest)
[![GitHub all releases](https://img.shields.io/github/downloads/self-crafted/velocity-sync/total?style=flat-square&label=downloads%20-%20GitHub)](https://github.com/self-crafted/velocity-sync/releases)
[![Modrinth Downloads](https://img.shields.io/modrinth/dt/EPawOqFJ?style=flat-square&label=downloads%20-%20Modrinth)](https://modrinth.com/plugin/velocity-sync/versions)

Dead simple sync between Minecraft Velocity proxies.

velocity-sync adds no commands to interface with it.
Events and information available to one proxy get synced to all others using MQTT.

> [!CAUTION]
> I used AI (Claude) to generate the starting point of the code.
> Even though I touched most of the code after that, there is still residue.

## Table of Contents

- [Install](#install)
- [Usage](#usage)
- [Build from source](#build-from-source)
- [Maintainers](#maintainers)
- [Contributing](#contributing)
- [License](#license)

## Install
Download a release from [GitHub](https://github.com/self-crafted/velocity-sync/releases),
[Modrinth](https://modrinth.com/plugin/velocity-sync/versions)
or [build the plugin from source](#build-from-source).

Put the `.jar` into the `plugins/` directory of the proxy.

## Usage
The file `allowlist.txt` is read every 10 seconds. So simply drop the UUIDs of friends and family in there.
You can write comments starting the line with `#` or `//`.
Player names can't be used. The plugin will accept UUIDs only.

```
# valid comment
<valid UUID>
// another valid comment
  <valid UUID>
```

## Build from source

You can compile the plugin yourself using the following commands under Linux.
```shell
git clone https://github.com/self-crafted/velocity-sync.git
cd velocity-sync
./gradlew build
```
The plugin jar will be located at `build/libs/velocity-sync-<VERSION>.jar`.

Note that for compiling you should use JDK 21, the minimum version for Velocity build 600 (latest at 30th May 2026).

## Maintainers

[@offby0point5](https://github.com/offby0point5)

## Contributing

If you've got suggestions, feel free to open an [issue](https://github.com/self-crafted/velocity-sync/issues).

PRs accepted.

Small note: If editing the README, please conform to the [standard-readme](https://github.com/RichardLitt/standard-readme) specification.

## License

This project is licensed under the [MIT license](LICENSE).
