COLORS = ["white",
          "orange",
          "magenta",
          "light_blue",
          "yellow",
          "lime",
          "pink",
          "gray",
          "light_gray",
          "cyan",
          "purple",
          "blue",
          "brown",
          "green",
          "red",
          "black"]

WOODS = ["oak", "spruce", "birch", "acacia", "cherry", "jungle", "dark_oak", "crimson", "warped", "mangrove", "bamboo"]


def langify(s: str) -> str:
    """
    Takes a string like dark_oak and converts it to Dark Oak.
    Yes this method is horribly named I'm having trouble coming up with a good one :|
    """
    return ' '.join([word.capitalize() for word in s.split('_')])
