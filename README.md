# canary-scala

This repo contains the scala implementation of canary, a command line tool used to scan and fix configuration errors.
The tool functions similarly to package managers like npm. You can install, upgrade, and check packages, or specify
a custom package from the filesystem.

Canary has a matching remote repository at [canary.wiles.fyi](https://canary.wiles.fyi). This site contains packages to be installed by this 
command line tool. The repository at [github.com/david-wiles/canary-remote](https://github.com/david-wiles/canary-remote)
contains the sources and archived files, along with a bash script to make packaging and deploying changes to the site 
easy on me.

You can install canary by running install.sh (this will require sudo to move )

Canary can be used as a tool during development or with devops integrations. It can be set to automatically fix issues
(which may be helpful during deployments) or to only scan for issues and report them to the terminal.

```install```: download a package from a remote canary repository and save to the filesystem. Also verifies checksum and unpacks tar archive

```upgrade```: checks the repository for any newer versions of specified packages and installs them

```check```: runs scripts from a package or directory, reports issues, and fixes them if applicable.

The folder for a package is as follows:

```
root
 | 
 |\_package
 |  |\_version
 |  |  |\_task
 |  |  |  \_analyze.sh
 |  |  |  \_solution.sh
 |  |  |  \_config
 |  |  |
 |  |  \_task
 |  |    \_analyze.sh
 |  |    \_solution.sh
 |  |    \_config
 |  |
 |  |\_version
 |  |  |\_task
 |  |  |  \_analyze.sh
 |  |  |  \_solution.sh
 |  |  |  \_config
 |  |  |
 |  |  \_task
 |  |    \_analyze.sh
 |  |    \_solution.sh
 |  |    \_config
 |
  \_package
    \_version
      |\_task
      |   \_analyze.sh
      |   \_solution.sh
      |   \_config
      |
       \_task
         \_analyze.sh
         \_solution.sh
         \_config
```

Each package contains additional directory for each version installed. Each directory contains tasks, which are also
stored in directories. Each task should contain a plaintext config file which has key-value mappings for different
attributes on the task. A task currently only has three attributes defined: solution, analyze, and description. Solution
maps to a file which should be used to fix the problem, analyze maps to a file to analyze whether an issue exists, and 
description provides a string to present to the user describing the issue.

In the future, the config file could be expanded to include additional attributes, such as environment variables to set
before task execution.