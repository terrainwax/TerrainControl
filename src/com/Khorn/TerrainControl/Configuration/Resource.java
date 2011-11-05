package com.Khorn.TerrainControl.Configuration;


import com.Khorn.TerrainControl.Util.ResourceType;
import com.Khorn.TerrainControl.Util.TreeType;
import org.bukkit.Material;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Resource
{
    public ResourceType Type;
    public int MinAltitude;
    public int MaxAltitude;
    public int Size;
    public int BlockId;
    public int BlockData;
    private int[] SourceBlockId = new int[0];
    public int Frequency;
    public int Rarity;
    public boolean Done = false;
    public TreeType[] TreeTypes = new TreeType[0];
    public int[] TreeChances = new int[0];


    public Resource(ResourceType type)
    {
        Type = type;
    }

    public Resource(ResourceType type, int blockId, int blockData, int size, int frequency, int rarity, int minAltitude, int maxAltitude, int[] sourceBlockIds)
    {
        this.Type = type;
        this.BlockId = blockId;
        this.BlockData = blockData;
        this.Size = size;
        this.Frequency = frequency;
        this.Rarity = rarity;
        this.MinAltitude = minAltitude;
        this.MaxAltitude = maxAltitude;
        this.SourceBlockId = sourceBlockIds;
        this.Done = true;

    }

    public Resource(ResourceType type, int frequency, TreeType[] types, int[] treeChances)
    {
        this.Type = type;
        this.Frequency = frequency;
        if (types != null)
        {
            this.TreeTypes = types;
            this.TreeChances = treeChances;
        }
        this.Done = true;

    }

    public boolean CheckSourceId(int blockId)
    {
        for (int id : this.SourceBlockId)
            if (blockId == id)
                return true;
        return false;
    }

    private int CheckValue(String str, int min, int max) throws NumberFormatException
    {
        int value = Integer.valueOf(str);
        if (value > max)
            return max;
        else if (value < min)
            return min;
        else
            return value;
    }

    private int CheckValue(String str, int min, int max, int minValue) throws NumberFormatException
    {
        int value = CheckValue(str, min, max);

        if (value < minValue)
            return minValue + 1;
        else
            return value;
    }

    private int CheckBlock(String block) throws NumberFormatException
    {
        Material mat = Material.getMaterial(block);
        if (mat != null && mat.isBlock())
            return mat.getId();

        return CheckValue(block, 0, 256);

    }

    public void ReadFromString(String line)
    {
        try
        {
            String[] Props = line.split(",");
            switch (this.Type)
            {
                case Ore:
                    if (Props.length < 7)
                        return;
                    this.BlockId = CheckBlock(Props[0]);
                    this.Size = CheckValue(Props[1], 1, 32);
                    this.Frequency = CheckValue(Props[2], 1, 100);
                    this.Rarity = CheckValue(Props[3], 0, 100);
                    this.MinAltitude = CheckValue(Props[4], 0, 128);
                    this.MaxAltitude = CheckValue(Props[5], 0, 128, this.MinAltitude);

                    this.SourceBlockId = new int[Props.length - 6];
                    for (int i = 6; i < Props.length; i++)
                        this.SourceBlockId[i - 6] = CheckBlock(Props[i]);
                    break;
                case UnderWaterOre:
                    if (Props.length < 5)
                        return;
                    this.BlockId = CheckBlock(Props[0]);
                    this.Size = CheckValue(Props[1], 1, 8);
                    this.Frequency = CheckValue(Props[2], 1, 100);
                    this.Rarity = CheckValue(Props[3], 0, 100);


                    this.SourceBlockId = new int[Props.length - 4];
                    for (int i = 4; i < Props.length; i++)
                        this.SourceBlockId[i - 4] = CheckBlock(Props[i]);
                    break;
                case Liquid:
                {
                    if (Props.length < 6)
                        return;
                    this.BlockId = CheckBlock(Props[0]);
                    this.Frequency = CheckValue(Props[1], 1, 100);
                    this.Rarity = CheckValue(Props[2], 0, 100);
                    this.MinAltitude = CheckValue(Props[3], 0, 128);
                    this.MaxAltitude = CheckValue(Props[4], 0, 128, this.MinAltitude);

                    this.SourceBlockId = new int[Props.length - 5];
                    for (int i = 5; i < Props.length; i++)
                        this.SourceBlockId[i - 5] = CheckBlock(Props[i]);
                    break;
                }
                case Grass:
                    if (Props.length < 5)
                        return;
                    this.BlockId = CheckBlock(Props[0]);
                    this.BlockData = CheckValue(Props[1], 0, 16);
                    this.Frequency = CheckValue(Props[2], 1, 100);
                    this.Rarity = CheckValue(Props[3], 0, 100);

                    this.SourceBlockId = new int[Props.length - 4];
                    for (int i = 4; i < Props.length; i++)
                        this.SourceBlockId[i - 4] = CheckBlock(Props[i]);

                    break;

                case Reed:
                case Cactus:
                case Plant:
                {
                    if (Props.length < 6)
                        return;
                    this.BlockId = CheckBlock(Props[0]);
                    this.Frequency = CheckValue(Props[1], 1, 100);
                    this.Rarity = CheckValue(Props[2], 0, 100);
                    this.MinAltitude = CheckValue(Props[3], 0, 128);
                    this.MaxAltitude = CheckValue(Props[4], 0, 128, this.MinAltitude);

                    this.SourceBlockId = new int[Props.length - 5];
                    for (int i = 5; i < Props.length; i++)
                        this.SourceBlockId[i - 5] = CheckBlock(Props[i]);

                    break;
                }
                case Dungeon:
                    if (Props.length != 4)
                        return;
                    this.Frequency = CheckValue(Props[0], 1, 100);
                    this.Rarity = CheckValue(Props[1], 0, 100);
                    this.MinAltitude = CheckValue(Props[2], 0, 128);
                    this.MaxAltitude = CheckValue(Props[3], 0, 128, this.MinAltitude);
                    break;
                case Tree:
                    if (Props.length < 3)
                        return;
                    this.Frequency = CheckValue(Props[0], 1, 100);

                    ArrayList<TreeType> treeTypes = new ArrayList<TreeType>();
                    ArrayList<Integer> treeChances = new ArrayList<Integer>();

                    for (int i = 1; i < Props.length && (i + 1) < Props.length; i += 2)
                    {
                        String tree = Props[i];
                        for (TreeType type : TreeType.values())
                            if (type.name().equals(tree))
                            {
                                treeTypes.add(type);
                                treeChances.add( CheckValue(Props[i + 1],0,100));
                            }
                    }
                    if (treeChances.size() > 0)
                    {
                        this.TreeTypes = new TreeType[treeChances.size()];
                        this.TreeChances = new int[treeChances.size()];
                        for (int t = 0; t < treeTypes.size(); t++)
                        {
                            this.TreeTypes[t] = treeTypes.get(t);
                            this.TreeChances[t] = treeChances.get(t);
                        }
                    } else
                    {
                        System.out.println("TerrainControl: wrong resource " + this.Type.name() + "(" + line + ")");
                        return;
                    }

                    break;
                case CustomObject:
                    if (Props.length != 1)
                        return;
                    break;
            }
        } catch (NumberFormatException e)
        {
            System.out.println("TerrainControl: wrong resource " + this.Type.name() + "(" + line + ")");
        }


        this.Done = true;


    }

    public String WriteToString()
    {
        String sources = "";
        for (int id : this.SourceBlockId)
            sources += "," + Material.getMaterial(id).name();
        String output = this.Type.name() + "(";

        switch (this.Type)
        {
            case Ore:
                output += Material.getMaterial(this.BlockId).name() + "," + this.Size + "," + this.Frequency + "," + this.Rarity + "," + this.MinAltitude + "," + this.MaxAltitude + sources + ")";
                break;
            case UnderWaterOre:
                output += Material.getMaterial(this.BlockId).name() + "," + this.Size + "," + this.Frequency + "," + this.Rarity + sources + ")";
                break;
            case Plant:
            case Liquid:
            case Reed:
            case Cactus:
                output += Material.getMaterial(this.BlockId).name() + "," + this.Frequency + "," + this.Rarity + "," + this.MinAltitude + "," + this.MaxAltitude + sources + ")";
                break;
            case Grass:
                output += Material.getMaterial(this.BlockId).name() + "," + this.BlockData + "," + this.Frequency + "," + this.Rarity + sources + ")";
                break;
            case Dungeon:
                output += this.Frequency + "," + this.Rarity + "," + this.MinAltitude + "," + this.MaxAltitude + ")";
                break;
            case Tree:
                output += this.Frequency ;
                for (int i = 0; i < this.TreeChances.length; i++)
                    output += "," + this.TreeTypes[i].name() + "," + this.TreeChances[i];
                output += ")";
                break;
            case CustomObject:
                output += ")";
                break;
        }
        return output;


    }
}