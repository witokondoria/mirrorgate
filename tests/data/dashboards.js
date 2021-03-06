db.getCollection('dashboards').insertMany([
    {
        "name":"mirrorgate",
        "logoUrl":"/mirrorgate/img/logo.png",
        "codeRepos":[
            "mirrorgate-app",
            "MirrorGate",
            "jira-collector",
            "design"
        ],
        "applications":[],
        "boards":[
            "MirrorGate"
        ]
    },
    {
        "name":"all-the-stuff",
        "displayName":"All the indicators",
        "logoUrl":"/mirrorgate/img/logo.png",
        "codeRepos":[
            "mirrorgate-app",
            "MirrorGate",
            "jira-collector",
            "design"
        ],
        "applications":['moods'],
        "boards":[
            "MirrorGate"
        ],
        'programIncrement': 'MG02'
    },
    {
        "name":"program-increment",
        "displayName":"Program Increment",
        "codeRepos":[
            "mirrorgate-app",
            "MirrorGate",
            "jira-collector",
            "design"
        ],
        "boards":[
            "MirrorGate"
        ],
        'programIncrement': '2017_PI03'
    },
    {
        "name":"nothing"
    },
    {
        "name":"empty",
        "codeRepos":[],
        "applications":[],
        "boards":[]
    },
    {
        "name":"all",
        "codeRepos":[".*"],
        "applications":[],
        "boards":[]
    },
    {
        "name":"mood",
        "applications":["Mood"]
    }
]);
