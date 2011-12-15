package net.markdevries.istephenhawking.utils;

public class BadWords
{
	private static final String TAG = BadWords.class.getSimpleName();
	
	private static final String[] badwords = {
			"ass", "tit", "cum","sex","dick","shit", "damn", "fuck","bitch", "piss", "asshole", "dickhead", "bastard","penis", "vagina", "boob", "cunt",  
			"clit", "anus", "pussy",  "blow job", "rectum", "slut", "whore", "prostitute", "testicles", "twat", 
			"titty","masturbate", "jack off", "scrotum","nipple", "nigger", "kike", "kyke", "gook", "spick", 
			"nazi","fag","queer","chink","dago", "white trash", "kkk", "ku klux klan", "jew", "faggot", "anus", "arse", "arsehole", "ass", "ass-hat", "ass-pirate", "assbag", "assbandit", "assbanger", "assbite", "assclown", "asscock", "asscracker", "asses", "assface", "assfuck", "assfucker", "assgoblin", "asshat", "asshead", "asshole", "asshopper", "assjacker", "asslick", "asslicker", "assmonkey", "assmunch", "assmuncher", "assnigger", "asspirate", "assshit", "assshole", "asssucker", "asswad", "asswipe", "bampot", "bastard", "beaner", "bitch", "bitchass", "bitches", "bitchtits", "bitchy", "blow job", "blowjob", "bollocks", "bollox", "boner", "brotherfucker", "bullshit", "bumblefuck", "butt plug", "butt-pirate", "buttfucka", "buttfucker", "camel toe", "carpetmuncher", "chinc", "chink", "choad", "chode", "clit", "clitface", "clitfuck", "clusterfuck", "cock", "cockass", "cockbite", "cockburger", "cockface", "cockfucker", "cockhead", "cockjockey", "cockknoker", "cockmaster", "cockmongler", "cockmongruel", "cockmonkey", "cockmuncher", "cocknose", "cocknugget", "cockshit", "cocksmith", "cocksmoker", "cocksucker", "coochie", "coochy", "coon", "cooter", "cracker", "cum", "cumbubble", "cumdumpster", "cumguzzler", "cumjockey", "cumslut", "cumtart", "cunnie", "cunnilingus", "cunt", "cuntface", "cunthole", "cuntlicker", "cuntrag", "cuntslut", "dago", "damn", "deggo", "dick", "dickbag", "dickbeaters", "dickface", "dickfuck", "dickfucker", "dickhead", "dickhole", "dickjuice", "dickmilk", "dickmonger", "dicks", "dickslap", "dicksucker", "dickwad", "dickweasel", "dickweed", "dickwod", "dike", "dildo", "dipshit", "doochbag", "dookie", "douche", "douche-fag", "douchebag", "douchewaffle", "dumass", "dumb ass", "dumbass", "dumbfuck", "dumbshit", "dumshit", "dyke", "fag", "fagbag", "fagfucker", "faggit", "faggot", "faggotcock", "fagtard", "fatass", "fellatio", "feltch", "flamer", "fuck", "fuckass", "fuckbag", "fuckboy", "fuckbrain", "fuckbutt", "fucked", "fucker", "fuckersucker", "fuckface", "fuckhead", "fuckhole", "fuckin", "fucking", "fucknut", "fucknutt", "fuckoff", "fucks", "fuckstick", "fucktard", "fuckup", "fuckwad", "fuckwit", "fuckwitt", "fudgepacker", "gay", "gayass", "gaybob", "gaydo", "gayfuck", "gayfuckist", "gaylord", "gaytard", "gaywad", "goddamn", "goddamnit", "gooch", "gook", "gringo", "guido", "handjob", "hard on", "heeb", "hell", "ho", "hoe", "homo", "homodumbshit", "honkey", "humping", "jackass", "jap", "jerk off", "jigaboo", "jizz", "jungle bunny", "junglebunny", "kike", "kooch", "kootch", "kunt", "kyke", "lesbian", "lesbo", "lezzie", "mcfagget", "mick", "minge", "mothafucka", "motherfucker", "motherfucking", "muff", "muffdiver", "munging", "negro", "nigga", "nigger", "niggers", "niglet", "nut sack", "nutsack", "Back To Top", "paki", "panooch", "pecker", "peckerhead", "penis", "penisfucker", "penispuffer", "piss", "pissed", "pissed off", "pissflaps", "polesmoker", "pollock", "poon", "poonani", "poonany", "poontang", "porch monkey", "porchmonkey", "prick", "punanny", "punta", "pussies", "pussy", "pussylicking", "puto", "queef", "queer", "queerbait", "queerhole", "renob", "rimjob", "ruski", "sand nigger", "sandnigger", "schlong", "scrote", "shit", "shitass", "shitbag", "shitbagger", "shitbrains", "shitbreath", "shitcunt", "shitdick", "shitface", "shitfaced", "shithead", "shithole", "shithouse", "shitspitter", "shitstain", "shitter", "shittiest", "shitting", "shitty", "shiz", "shiznit", "skank", "skeet", "skullfuck", "slut", "slutbag", "smeg", "snatch", "spic", "spick", "splooge", "tard", "testicle", "thundercunt", "tit", "titfuck", "tits", "tittyfuck", "twat", "twatlips", "twats", "twatwaffle", "unclefucker", "va-j-j", "vag", "vagina", "vjayjay", "wank", "wetback", "whore", "whorebag", "whoreface", "wop",
			"andrew", "andy", "keller", "keler", "rob", "robert", "reilly", "rielly", "bejamin", "jef", "geoff", "geof", "geoffry", "geoffery", "jeff", "jeffrey", "jeffery", "crispin", "porter", "bogusky", "alex", "chuck", "charles",
			"alexander", "graham", "schiff", "shiff", "shif", "david", "dave", "dav", "prindle", "scott", "rolf", "tif", "tiffany", "stein", "stien", "steinhour", "stienhour", "bernie", "bernard", "burnard",
			"cpb", "cp+b"
	};
	
	public static boolean hasBadWords( String str )
	{
		boolean results = false;
		str = str.replaceAll("[^A-Za-z0-9 ]", "");
		String[] words = str.split(" ");
		for( String word : words )
		{
			for( String badword : badwords )
			{
				if( word.toLowerCase().equals( badword.toLowerCase() ) )
				{
					results = true;
					break;
				}
			}
			
			if( results == true )
				break;
		}
		return results;
	}
}
